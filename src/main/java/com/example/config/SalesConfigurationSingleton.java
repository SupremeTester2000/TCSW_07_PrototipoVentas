package com.example.config;

import java.math.BigDecimal;

public final class SalesConfigurationSingleton {

    private volatile BigDecimal vatRate = new BigDecimal("0.16");
    private volatile BigDecimal maximumDiscountAmount = new BigDecimal("100000.00");
    private volatile String baseCurrency = "MXN";
    private volatile int maximumSaleDetails = 100;

    private SalesConfigurationSingleton() {
    }

    private static class Holder {
        private static final SalesConfigurationSingleton INSTANCE = new SalesConfigurationSingleton();
    }

    public static SalesConfigurationSingleton getInstance() {
        return Holder.INSTANCE;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        if (vatRate == null || vatRate.compareTo(BigDecimal.ZERO) < 0
                || vatRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("La tasa de IVA debe estar entre cero y uno.");
        }
        this.vatRate = vatRate;
    }

    public BigDecimal getMaximumDiscountAmount() {
        return maximumDiscountAmount;
    }

    public void setMaximumDiscountAmount(BigDecimal maximumDiscountAmount) {
        if (maximumDiscountAmount == null || maximumDiscountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento máximo no puede ser nulo ni negativo.");
        }
        this.maximumDiscountAmount = maximumDiscountAmount;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        if (baseCurrency == null || baseCurrency.trim().isEmpty()) {
            throw new IllegalArgumentException("La moneda base no puede estar vacía.");
        }
        this.baseCurrency = baseCurrency.trim();
    }

    public int getMaximumSaleDetails() {
        return maximumSaleDetails;
    }

    public void setMaximumSaleDetails(int maximumSaleDetails) {
        if (maximumSaleDetails <= 0) {
            throw new IllegalArgumentException("El límite de detalles debe ser mayor a cero.");
        }
        this.maximumSaleDetails = maximumSaleDetails;
    }
}