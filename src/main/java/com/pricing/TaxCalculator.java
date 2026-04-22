package com.pricing;

/**
 * Responsible only for calculating tax.
 */
public class TaxCalculator {

    private static final double TAX_RATE = 0.15;

    /**
     * Returns the tax amount for a given taxable amount.
     */
    public double calculate(double taxableAmount) {
        return taxableAmount * TAX_RATE;
    }
}
