package com.example.config;

import java.math.BigDecimal;

public final class ConfiguracionVentasSingleton {

    private volatile BigDecimal ivaRate = new BigDecimal("0.16");
    private volatile BigDecimal maximumDiscountAmount = new BigDecimal("100000.00");
    private volatile String baseCurrency = "MXN";
    private volatile int maximumSaleDetails = 100;

    private ConfiguracionVentasSingleton() {
    }

    private static class Holder {
        private static final ConfiguracionVentasSingleton INSTANCE = new ConfiguracionVentasSingleton();
    }

    public static ConfiguracionVentasSingleton getInstance() {
        return Holder.INSTANCE;
    }

    public BigDecimal getIvaRate() {
        return ivaRate;
    }

    public void setIvaRate(BigDecimal ivaRate) {
        if (ivaRate == null || ivaRate.compareTo(BigDecimal.ZERO) < 0
                || ivaRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("La tasa de IVA debe estar entre cero y uno.");
        }
        this.ivaRate = ivaRate;
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