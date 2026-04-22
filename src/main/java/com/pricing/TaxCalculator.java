package com.pricing;

/**
 * TaxCalculator computes the tax amount for a given taxable amount.
 *
 * Tax rate is fixed at 15% (VAT-style flat rate).
 */
public class TaxCalculator {

    /** Flat tax rate applied to the post-discount amount. */
    private static final double TAX_RATE = 0.15;

    /**
     * Returns the tax amount for the given taxable amount.
     *
     * @param taxableAmount the amount after discounts have been applied
     * @return tax amount (taxableAmount × TAX_RATE)
     */
    public double calculate(double taxableAmount) {
        return taxableAmount * TAX_RATE;
    }
}
