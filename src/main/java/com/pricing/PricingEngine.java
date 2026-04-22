package com.pricing;

import java.util.List;

/**
 * PricingEngine orchestrates the full order price calculation.
 *
 * Responsibilities:
 *   - Compute subtotal from item prices and quantities
 *   - Delegate discount logic to DiscountCalculator
 *   - Delegate tax logic to TaxCalculator
 *   - Return a complete OrderSummary
 */
public class PricingEngine {

    private final DiscountCalculator discountCalculator;
    private final TaxCalculator      taxCalculator;

    public PricingEngine() {
        this.discountCalculator = new DiscountCalculator();
        this.taxCalculator      = new TaxCalculator();
    }

    /**
     * Calculates the full order breakdown and prints it to stdout.
     *
     * @param prices       unit price for each item
     * @param quantities   quantity for each item (parallel to prices)
     * @param customerType "REGULAR" or "VIP"
     * @param discountCode promotional code (e.g. "SAVE10"), or null
     * @return final price after discount and tax
     */
    public double calc(List<Double>  prices,
                       List<Integer> quantities,
                       String        customerType,
                       String        discountCode) {

        OrderSummary summary = calculateOrder(prices, quantities, customerType, discountCode);
        System.out.println(summary);
        return summary.getFinalPrice();
    }

    /**
     * Returns the subtotal (sum of price × quantity for all items).
     */
    public double getSubtotal(List<Double> prices, List<Integer> quantities) {
        double subtotal = 0.0;
        for (int i = 0; i < prices.size(); i++) {
            subtotal += prices.get(i) * quantities.get(i);
        }
        return subtotal;
    }

    /**
     * Returns the discount amount for a given subtotal and discount code.
     * Does NOT include the VIP customer discount — use calculateOrder for the full picture.
     */
    public double getDiscount(double subtotal, String discountCode) {
        return discountCalculator.getCodeDiscount(subtotal, discountCode);
    }

    /**
     * Returns the tax amount for a given taxable amount.
     */
    public double getTax(double taxableAmount) {
        return taxCalculator.calculate(taxableAmount);
    }

    /**
     * Full calculation: subtotal → discount → tax → final price.
     *
     * @return OrderSummary with all breakdown fields populated
     */
    public OrderSummary calculateOrder(List<Double>  prices,
                                       List<Integer> quantities,
                                       String        customerType,
                                       String        discountCode) {

        double subtotal    = getSubtotal(prices, quantities);
        double discount    = discountCalculator.getTotalDiscount(subtotal, customerType, discountCode);
        double taxable     = subtotal - discount;
        double tax         = taxCalculator.calculate(taxable);
        double finalPrice  = taxable + tax;

        return new OrderSummary(subtotal, discount, tax, finalPrice);
    }
}
