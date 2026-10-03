package com.example.domain.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Function;

import com.example.config.SalesConfigurationSingleton;
import com.example.domain.model.Sale;

public final class FunctionalDiscountPolicies {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private FunctionalDiscountPolicies() {
    }

    public static Function<Sale, BigDecimal> percentage(BigDecimal percentage) {
        if (percentage == null || percentage.compareTo(BigDecimal.ZERO) < 0
                || percentage.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException("El porcentaje debe estar entre cero y cien.");
        }
        return sale -> {
            Objects.requireNonNull(sale, "La venta no puede ser nula.");
            BigDecimal discount = sale.getSubtotal()
                    .multiply(percentage)
                    .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
            return discount.min(sale.getSubtotal())
                    .min(SalesConfigurationSingleton.getInstance().getMaximumDiscountAmount())
                    .setScale(2, RoundingMode.HALF_UP);
        };
    }

    public static Function<Sale, BigDecimal> fixed(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento fijo no puede ser nulo ni negativo.");
        }
        return sale -> {
            Objects.requireNonNull(sale, "La venta no puede ser nula.");
            return amount.min(sale.getSubtotal())
                    .min(SalesConfigurationSingleton.getInstance().getMaximumDiscountAmount())
                    .setScale(2, RoundingMode.HALF_UP);
        };
    }
}