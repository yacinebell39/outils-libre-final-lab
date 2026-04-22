package com.pricing;

/**
 * Responsible only for calculating discounts.
 * Supports discount codes and customer type discounts.
 */
public class DiscountCalculator {

    private static final double SAVE5_RATE  = 0.05;
    private static final double SAVE10_RATE = 0.10;
    private static final double SAVE20_RATE = 0.20;
    private static final double VIP_RATE    = 0.05;

    /**
     * Returns the discount amount based on the discount code.
     */
    public double getCodeDiscount(double subtotal, String discountCode) {
        if (discountCode == null) return 0.0;

        switch (discountCode) {
            case "SAVE5":  return subtotal * SAVE5_RATE;
            case "SAVE10": return subtotal * SAVE10_RATE;
            case "SAVE20": return subtotal * SAVE20_RATE;
            default:       return 0.0;
        }
    }

    /**
     * Returns the extra discount based on customer type.
     */
    public double getCustomerDiscount(double subtotal, String customerType) {
        if ("VIP".equals(customerType)) {
            return subtotal * VIP_RATE;
        }
        return 0.0;
    }

    /**
     * Returns the total discount (code + customer type).
     */
    public double getTotalDiscount(double subtotal, String customerType, String discountCode) {
        return getCodeDiscount(subtotal, discountCode)
             + getCustomerDiscount(subtotal, customerType);
    }
}
