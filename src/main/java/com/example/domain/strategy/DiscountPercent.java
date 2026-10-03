package com.example.domain.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.example.config.SalesConfigurationSingleton;
import com.example.domain.model.Sale;

public final class DiscountPercent implements DiscountStrategy {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private final BigDecimal percentage;

    public DiscountPercent(BigDecimal percentage) {
        if (percentage == null || percentage.compareTo(BigDecimal.ZERO) < 0
                || percentage.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException("El porcentaje debe estar entre cero y cien.");
        }
        this.percentage = percentage;
    }

    @Override
    public BigDecimal calculateDiscount(Sale sale) {
        Objects.requireNonNull(sale, "La venta no puede ser nula.");
        BigDecimal calculated = sale.getSubtotal()
                .multiply(percentage)
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
        return calculated.min(sale.getSubtotal())
                .min(SalesConfigurationSingleton.getInstance().getMaximumDiscountAmount())
                .setScale(2, RoundingMode.HALF_UP);
    }
}