package com.pricing;

import java.util.ArrayList;
import java.util.List;

/**
 * Command-line entry point used by the Python integration tests.
 *
 * Usage:
 *   java -cp build/libs/pricing-engine.jar com.pricing.IntegrationRunner \
 *        <prices_csv> <quantities_csv> <customer_type> <discount_code|NONE>
 *
 * Example:
 *   java -cp ... com.pricing.IntegrationRunner 100.0,50.0 2,3 VIP SAVE10
 */
public class IntegrationRunner {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: IntegrationRunner <prices> <quantities> <customerType> <discountCode|NONE>");
            System.exit(1);
        }

        List<Double>  prices     = parsePrices(args[0]);
        List<Integer> quantities = parseQuantities(args[1]);
        String        customer   = args[2];
        String        code       = "NONE".equals(args[3]) ? null : args[3];

        PricingEngine engine  = new PricingEngine();
        OrderSummary  summary = engine.calculateOrder(prices, quantities, customer, code);

        System.out.println(summary);
    }

    private static List<Double> parsePrices(String csv) {
        List<Double> list = new ArrayList<>();
        for (String token : csv.split(",")) {
            list.add(Double.parseDouble(token.trim()));
        }
        return list;
    }

    private static List<Integer> parseQuantities(String csv) {
        List<Integer> list = new ArrayList<>();
        for (String token : csv.split(",")) {
            list.add(Integer.parseInt(token.trim()));
        }
        return list;
    }
}
