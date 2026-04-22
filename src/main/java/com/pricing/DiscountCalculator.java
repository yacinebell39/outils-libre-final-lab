package com.pricing;

/**
 * DiscountCalculator applies promotional codes and customer-type discounts.
 *
 * Supported discount codes:
 *   SAVE5  →  5%
 *   SAVE10 → 10%
 *   SAVE20 → 20%
 *
 * Supported customer types:
 *   VIP    →  5% extra discount on top of any code discount
 */
public class DiscountCalculator {

    private static final double SAVE5_RATE   = 0.05;
    private static final double SAVE10_RATE  = 0.10;
    private static final double SAVE20_RATE  = 0.20;
    private static final double VIP_DISCOUNT = 0.05;

    /**
     * Returns the discount amount based on the promotional code alone.
     *
     * @param subtotal     the order subtotal before any discount
     * @param discountCode the promotional code, or null for no code
     * @return discount amount (0.0 if code is unknown or null)
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
     * Returns the extra discount granted to VIP customers.
     *
     * @param subtotal     the order subtotal before any discount
     * @param customerType "VIP" or "REGULAR"
     * @return discount amount (0.0 for REGULAR customers)
     */
    public double getCustomerDiscount(double subtotal, String customerType) {
        if ("VIP".equals(customerType)) {
            return subtotal * VIP_DISCOUNT;
        }
        return 0.0;
    }

    /**
     * Returns the combined discount from both the promotional code
     * and the customer type.
     *
     * @param subtotal     the order subtotal before any discount
     * @param customerType "VIP" or "REGULAR"
     * @param discountCode promotional code, or null
     * @return total discount amount
     */
    public double getTotalDiscount(double subtotal, String customerType, String discountCode) {
        return getCodeDiscount(subtotal, discountCode)
             + getCustomerDiscount(subtotal, customerType);
    }
}
