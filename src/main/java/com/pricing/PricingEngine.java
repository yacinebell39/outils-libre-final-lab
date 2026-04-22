package com.pricing;

import java.util.List;

// BAD DESIGN - everything in one class, poor naming, no separation of concerns
public class PricingEngine {

    // bad variable names
    public double calc(List<Double> p, List<Integer> q, String ct, String code) {
        double tot = 0;
        for (int i = 0; i < p.size(); i++) {
            tot = tot + p.get(i) * q.get(i);
        }

        double d = 0;
        if (code != null) {
            if (code.equals("SAVE10")) {
                d = tot * 0.10;
            } else if (code.equals("SAVE20")) {
                d = tot * 0.20;
            } else if (code.equals("SAVE5")) {
                d = tot * 0.05;
            }
        }

        // extra discount for VIP - magic number, no explanation
        if (ct != null && ct.equals("VIP")) {
            d = d + tot * 0.05;
        }

        double x = tot - d;

        // tax - magic number
        double tx = x * 0.15;

        double f = x + tx;

        // print everything (mixed responsibilities)
        System.out.println("Subtotal: " + tot);
        System.out.println("Discount: " + d);
        System.out.println("Tax: " + tx);
        System.out.println("Final: " + f);

        return f;
    }

    // duplicate logic, inconsistent
    public double getSubtotal(List<Double> prices, List<Integer> quantities) {
        double total = 0;
        for (int i = 0; i < prices.size(); i++) {
            total += prices.get(i) * quantities.get(i);
        }
        return total;
    }

    public double getDiscount(double tot, String c) {
        if (c == null) return 0;
        if (c == "SAVE10") return tot * 0.10; // BUG: using == for String comparison
        if (c == "SAVE20") return tot * 0.20;
        return 0;
    }

    public double getTax(double amount) {
        return amount * 0.15; // magic number, no constant
    }
}
