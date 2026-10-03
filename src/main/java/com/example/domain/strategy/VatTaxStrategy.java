package com.example.domain.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.example.config.SalesConfigurationSingleton;
import com.example.domain.model.Sale;

public final class VatTaxStrategy implements TaxStrategy {

    @Override
    public BigDecimal calculateTax(Sale sale, BigDecimal taxableAmount) {
        Objects.requireNonNull(sale, "La venta no puede ser nula.");
        Objects.requireNonNull(taxableAmount, "La base gravable no puede ser nula.");
        return taxableAmount
                .multiply(SalesConfigurationSingleton.getInstance().getVatRate())
                .setScale(2, RoundingMode.HALF_UP);
    }
}