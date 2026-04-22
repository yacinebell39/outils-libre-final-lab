package com.pricing;

import java.util.Arrays;
import java.util.List;

/**
 * Entry point — demonstrates the PricingEngine with a sample order.
 */
public class Main {

    public static void main(String[] args) {
        PricingEngine engine = new PricingEngine();

        List<Double>  prices     = Arrays.asList(50.0, 30.0, 20.0);
        List<Integer> quantities = Arrays.asList(2, 3, 1);
        String        customer   = "VIP";
        String        code       = "SAVE10";

        System.out.println("=== Pricing Engine Demo ===");
        System.out.printf("Customer : %s%n", customer);
        System.out.printf("Code     : %s%n", code);
        System.out.println("---------------------------");

        engine.calc(prices, quantities, customer, code);
    }
}
