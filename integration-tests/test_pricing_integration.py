"""
Integration tests for the Pricing Engine.

These tests compile and run the Java JAR directly via subprocess,
then verify the printed output matches expected values.

Usage:
    # From the project root:
    python integration-tests/test_pricing_integration.py
"""

import subprocess
import sys
import os
import unittest


# ── Helpers ────────────────────────────────────────────────────────────────────

def build_jar():
    """Run 'gradle jar' to produce the fat/shadow jar (or plain jar)."""
    result = subprocess.run(
        ["./gradlew", "jar"],
        capture_output=True, text=True
    )
    if result.returncode != 0:
        print("BUILD FAILED:\n", result.stderr)
        sys.exit(1)


def find_jar():
    """Return the path to the built jar file."""
    build_dir = os.path.join("build", "libs")
    jars = [f for f in os.listdir(build_dir) if f.endswith(".jar")]
    if not jars:
        raise FileNotFoundError("No JAR found in build/libs — did you run build_jar()?")
    return os.path.join(build_dir, jars[0])


def run_engine(prices, quantities, customer_type, discount_code, jar_path):
    """
    Invokes the Java pricing engine via a helper main class that accepts
    command-line arguments:
        java -cp <jar> com.pricing.IntegrationRunner
             <prices_csv> <quantities_csv> <customer_type> <discount_code>

    Returns the stdout as a string.
    """
    prices_arg      = ",".join(str(p) for p in prices)
    quantities_arg  = ",".join(str(q) for q in quantities)
    discount_arg    = discount_code if discount_code else "NONE"

    result = subprocess.run(
        [
            "java", "-cp", jar_path,
            "com.pricing.IntegrationRunner",
            prices_arg, quantities_arg, customer_type, discount_arg
        ],
        capture_output=True, text=True
    )
    return result.stdout.strip()


def parse_output(output):
    """
    Parse the 4-line output of OrderSummary.toString():
        Subtotal : 200.00
        Discount : 20.00
        Tax      : 27.00
        Final    : 207.00
    Returns a dict with float values.
    """
    result = {}
    for line in output.splitlines():
        if ":" in line:
            key, value = line.split(":", 1)
            result[key.strip().lower()] = float(value.strip())
    return result


# ── Integration Test Class ─────────────────────────────────────────────────────

class PricingEngineIntegrationTest(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        """Build the JAR once before all tests."""
        print("\n[Setup] Building JAR ...")
        build_jar()
        cls.jar = find_jar()
        print(f"[Setup] Using JAR: {cls.jar}\n")

    def _run(self, prices, quantities, customer, code):
        output = run_engine(prices, quantities, customer, code, self.jar)
        return parse_output(output)

    # ── Regular customer, no discount ──────────────────────────────────────────

    def test_regular_no_discount(self):
        """subtotal=100, discount=0, tax=15, final=115"""
        r = self._run([100.0], [1], "REGULAR", None)
        self.assertAlmostEqual(r["subtotal"], 100.0, places=2)
        self.assertAlmostEqual(r["discount"],   0.0, places=2)
        self.assertAlmostEqual(r["tax"],        15.0, places=2)
        self.assertAlmostEqual(r["final"],     115.0, places=2)

    # ── Regular customer, SAVE10 ───────────────────────────────────────────────

    def test_regular_save10(self):
        """subtotal=200, discount=20, taxable=180, tax=27, final=207"""
        r = self._run([100.0, 100.0], [1, 1], "REGULAR", "SAVE10")
        self.assertAlmostEqual(r["subtotal"], 200.0, places=2)
        self.assertAlmostEqual(r["discount"],  20.0, places=2)
        self.assertAlmostEqual(r["tax"],       27.0, places=2)
        self.assertAlmostEqual(r["final"],    207.0, places=2)

    # ── Regular customer, SAVE20 ───────────────────────────────────────────────

    def test_regular_save20(self):
        """subtotal=100, discount=20, taxable=80, tax=12, final=92"""
        r = self._run([100.0], [1], "REGULAR", "SAVE20")
        self.assertAlmostEqual(r["subtotal"], 100.0, places=2)
        self.assertAlmostEqual(r["discount"],  20.0, places=2)
        self.assertAlmostEqual(r["tax"],       12.0, places=2)
        self.assertAlmostEqual(r["final"],     92.0, places=2)

    # ── VIP customer, no code ──────────────────────────────────────────────────

    def test_vip_no_discount_code(self):
        """subtotal=100, VIP=5, taxable=95, tax=14.25, final=109.25"""
        r = self._run([100.0], [1], "VIP", None)
        self.assertAlmostEqual(r["subtotal"],  100.0,  places=2)
        self.assertAlmostEqual(r["discount"],    5.0,  places=2)
        self.assertAlmostEqual(r["tax"],        14.25, places=2)
        self.assertAlmostEqual(r["final"],     109.25, places=2)

    # ── VIP customer, SAVE20 ───────────────────────────────────────────────────

    def test_vip_save20(self):
        """subtotal=100, discount=25 (20+5), taxable=75, tax=11.25, final=86.25"""
        r = self._run([100.0], [1], "VIP", "SAVE20")
        self.assertAlmostEqual(r["subtotal"], 100.0,  places=2)
        self.assertAlmostEqual(r["discount"],  25.0,  places=2)
        self.assertAlmostEqual(r["tax"],       11.25, places=2)
        self.assertAlmostEqual(r["final"],     86.25, places=2)

    # ── Multiple items ─────────────────────────────────────────────────────────

    def test_multiple_items_regular_save10(self):
        """prices=[50,30] qty=[2,3] → subtotal=190, SAVE10=19,
           taxable=171, tax=25.65, final=196.65"""
        r = self._run([50.0, 30.0], [2, 3], "REGULAR", "SAVE10")
        self.assertAlmostEqual(r["subtotal"], 190.0,  places=2)
        self.assertAlmostEqual(r["discount"],  19.0,  places=2)
        self.assertAlmostEqual(r["tax"],       25.65, places=2)
        self.assertAlmostEqual(r["final"],    196.65, places=2)

    # ── Zero quantity ──────────────────────────────────────────────────────────

    def test_zero_quantity(self):
        """If quantity is 0, subtotal=0 and everything else=0"""
        r = self._run([100.0], [0], "REGULAR", "SAVE10")
        self.assertAlmostEqual(r["subtotal"], 0.0, places=2)
        self.assertAlmostEqual(r["discount"], 0.0, places=2)
        self.assertAlmostEqual(r["tax"],      0.0, places=2)
        self.assertAlmostEqual(r["final"],    0.0, places=2)


# ── Entry point ────────────────────────────────────────────────────────────────

if __name__ == "__main__":
    unittest.main(verbosity=2)
