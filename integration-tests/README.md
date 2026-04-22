# Integration Tests — Pricing Engine

These Python tests verify the full system end-to-end by compiling
and running the Java JAR via subprocess.

## Requirements
- Python 3.8+
- Java 17+
- Gradle (via `./gradlew`)

## How to Run

```bash
# From the project root:
python integration-tests/test_pricing_integration.py
```

## What is Tested

| Test                          | Customer | Code   | Expected Final |
|-------------------------------|----------|--------|---------------|
| Regular, no discount          | REGULAR  | —      | 115.00        |
| Regular, SAVE10               | REGULAR  | SAVE10 | 207.00        |
| Regular, SAVE20               | REGULAR  | SAVE20 | 92.00         |
| VIP, no code                  | VIP      | —      | 109.25        |
| VIP, SAVE20                   | VIP      | SAVE20 | 86.25         |
| Multiple items, SAVE10        | REGULAR  | SAVE10 | 196.65        |
| Zero quantity                 | REGULAR  | SAVE10 | 0.00          |

## Flow

```
Python test → subprocess → java -cp JAR IntegrationRunner → stdout → parse → assert
```
