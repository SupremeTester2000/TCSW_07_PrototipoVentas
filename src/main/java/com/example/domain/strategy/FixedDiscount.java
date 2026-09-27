package com.example.domain.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.example.config.ConfiguracionVentasSingleton;
import com.example.domain.model.Sale;

public final class FixedDiscount implements DiscountStrategy {

    private final BigDecimal amount;

    public FixedDiscount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento fijo no puede ser nulo ni negativo.");
        }
        this.amount = amount;
    }

    @Override
    public BigDecimal calculateDiscount(Sale sale) {
        Objects.requireNonNull(sale, "La venta no puede ser nula.");
        return amount.min(sale.getSubtotal())
                .min(ConfiguracionVentasSingleton.getInstance().getMaximumDiscountAmount())
                .setScale(2, RoundingMode.HALF_UP);
    }
}