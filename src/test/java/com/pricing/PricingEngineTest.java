package com.pricing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class PricingEngineTest {

    private PricingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new PricingEngine();
    }

    // ─── Subtotal Tests ───────────────────────────────────────────

    @Test
    void testSubtotal_singleItem() {
        List<Double> prices = Arrays.asList(100.0);
        List<Integer> quantities = Arrays.asList(2);
        double result = engine.getSubtotal(prices, quantities);
        assertEquals(200.0, result, 0.001);
    }

    @Test
    void testSubtotal_multipleItems() {
        List<Double> prices = Arrays.asList(10.0, 20.0, 30.0);
        List<Integer> quantities = Arrays.asList(1, 2, 3);
        double result = engine.getSubtotal(prices, quantities);
        // 10*1 + 20*2 + 30*3 = 10 + 40 + 90 = 140
        assertEquals(140.0, result, 0.001);
    }

    @Test
    void testSubtotal_zeroQuantity() {
        List<Double> prices = Arrays.asList(50.0);
        List<Integer> quantities = Arrays.asList(0);
        double result = engine.getSubtotal(prices, quantities);
        assertEquals(0.0, result, 0.001);
    }

    // ─── Discount Tests ───────────────────────────────────────────

    @Test
    void testDiscount_SAVE10() {
        double subtotal = 200.0;
        double discount = engine.getDiscount(subtotal, "SAVE10");
        assertEquals(20.0, discount, 0.001);
    }

    @Test
    void testDiscount_SAVE20() {
        double subtotal = 200.0;
        double discount = engine.getDiscount(subtotal, "SAVE20");
        assertEquals(40.0, discount, 0.001);
    }

    @Test
    void testDiscount_noCode() {
        double subtotal = 200.0;
        double discount = engine.getDiscount(subtotal, null);
        assertEquals(0.0, discount, 0.001);
    }

    @Test
    void testDiscount_invalidCode() {
        double subtotal = 200.0;
        double discount = engine.getDiscount(subtotal, "INVALIDCODE");
        assertEquals(0.0, discount, 0.001);
    }

    // ─── Tax Tests ────────────────────────────────────────────────

    @Test
    void testTax_standardAmount() {
        double tax = engine.getTax(100.0);
        assertEquals(15.0, tax, 0.001);
    }

    @Test
    void testTax_zero() {
        double tax = engine.getTax(0.0);
        assertEquals(0.0, tax, 0.001);
    }

    @Test
    void testTax_afterDiscount() {
        // subtotal=200, discount=20 => taxable=180 => tax=27
        double tax = engine.getTax(180.0);
        assertEquals(27.0, tax, 0.001);
    }

    // ─── Final Price Tests (calc) ─────────────────────────────────

    @Test
    void testFinalPrice_regularCustomer_noDiscount() {
        List<Double> prices = Arrays.asList(100.0);
        List<Integer> quantities = Arrays.asList(1);
        // subtotal=100, discount=0, taxable=100, tax=15, final=115
        double result = engine.calc(prices, quantities, "REGULAR", null);
        assertEquals(115.0, result, 0.001);
    }

    @Test
    void testFinalPrice_regularCustomer_SAVE10() {
        List<Double> prices = Arrays.asList(100.0);
        List<Integer> quantities = Arrays.asList(1);
        // subtotal=100, discount=10, taxable=90, tax=13.5, final=103.5
        double result = engine.calc(prices, quantities, "REGULAR", "SAVE10");
        assertEquals(103.5, result, 0.001);
    }

    @Test
    void testFinalPrice_VIP_noDiscountCode() {
        List<Double> prices = Arrays.asList(100.0);
        List<Integer> quantities = Arrays.asList(1);
        // subtotal=100, VIP discount=5%, taxable=95, tax=14.25, final=109.25
        double result = engine.calc(prices, quantities, "VIP", null);
        assertEquals(109.25, result, 0.001);
    }

    @Test
    void testFinalPrice_VIP_withSAVE20() {
        List<Double> prices = Arrays.asList(100.0);
        List<Integer> quantities = Arrays.asList(1);
        // subtotal=100, SAVE20=20 + VIP=5 => discount=25, taxable=75, tax=11.25, final=86.25
        double result = engine.calc(prices, quantities, "VIP", "SAVE20");
        assertEquals(86.25, result, 0.001);
    }

    @Test
    void testFinalPrice_multipleItems_SAVE10() {
        List<Double> prices = Arrays.asList(50.0, 30.0);
        List<Integer> quantities = Arrays.asList(2, 3);
        // subtotal = 100+90 = 190, SAVE10=19, taxable=171, tax=25.65, final=196.65
        double result = engine.calc(prices, quantities, "REGULAR", "SAVE10");
        assertEquals(196.65, result, 0.001);
    }
}
