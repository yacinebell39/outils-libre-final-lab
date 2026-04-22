package com.pricing;

import java.util.List;

/**
 * Refactored PricingEngine.
 * Delegates discount and tax logic to dedicated classes.
 * Keeps the old public API (calc, getSubtotal, getDiscount, getTax)
 * so existing tests still pass.
 */
public class PricingEngine {

    private final DiscountCalculator discountCalculator;
    private final TaxCalculator      taxCalculator;

    public PricingEngine() {
        this.discountCalculator = new DiscountCalculator();
        this.taxCalculator      = new TaxCalculator();
    }

    // ─── Public API (kept for backward compatibility with tests) ──

    public double calc(List<Double> prices,
                       List<Integer> quantities,
                       String customerType,
                       String discountCode) {

        OrderSummary summary = calculateOrder(prices, quantities, customerType, discountCode);
        System.out.println(summary);
        return summary.getFinalPrice();
    }

    public double getSubtotal(List<Double> prices, List<Integer> quantities) {
        double subtotal = 0.0;
        for (int i = 0; i < prices.size(); i++) {
            subtotal += prices.get(i) * quantities.get(i);
        }
        return subtotal;
    }

    public double getDiscount(double subtotal, String discountCode) {
        return discountCalculator.getCodeDiscount(subtotal, discountCode);
    }

    public double getTax(double taxableAmount) {
        return taxCalculator.calculate(taxableAmount);
    }

    // ─── New clean method returning full breakdown ─────────────────

    public OrderSummary calculateOrder(List<Double> prices,
                                       List<Integer> quantities,
                                       String customerType,
                                       String discountCode) {

        double subtotal   = getSubtotal(prices, quantities);
        double discount   = discountCalculator.getTotalDiscount(subtotal, customerType, discountCode);
        double taxable    = subtotal - discount;
        double tax        = taxCalculator.calculate(taxable);
        double finalPrice = taxable + tax;

        return new OrderSummary(subtotal, discount, tax, finalPrice);
    }
}
