package com.example.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.example.config.SalesConfigurationSingleton;
import com.example.domain.strategy.FixedDiscount;
import com.example.domain.strategy.TaxStrategy;
import com.example.domain.strategy.VatTaxStrategy;

public class Sale {

    private final List<SaleDetail> details = new ArrayList<>();
    private final Function<Sale, BigDecimal> discountPolicy;
    private final TaxStrategy taxStrategy;

    public Sale() {
        this(new FixedDiscount(BigDecimal.ZERO), new VatTaxStrategy());
    }

    public Sale(Function<Sale, BigDecimal> discountPolicy, TaxStrategy taxStrategy) {
        this.discountPolicy = Objects.requireNonNull(discountPolicy, "La política de descuento es obligatoria.");
        this.taxStrategy = Objects.requireNonNull(taxStrategy, "La estrategia de impuesto es obligatoria.");
    }

    public void addDetail(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (quantity > product.getStock()) {
            throw new IllegalArgumentException("La cantidad solicitada supera las existencias disponibles.");
        }
        if (details.size() >= SalesConfigurationSingleton.getInstance().getMaximumSaleDetails()) {
            throw new IllegalArgumentException("La venta supera el límite de detalles configurado.");
        }

        SaleDetail newDetail = new SaleDetail(product, quantity);
        details.add(newDetail);
    }

    public void addDetail(SaleDetail detail) {
        if (detail == null) {
            throw new IllegalArgumentException("El detalle de venta no puede ser nulo.");
        }
        if (details.size() >= SalesConfigurationSingleton.getInstance().getMaximumSaleDetails()) {
            throw new IllegalArgumentException("La venta supera el límite de detalles configurado.");
        }
        details.add(detail);
    }

    public List<SaleDetail> getDetails() {
        return Collections.unmodifiableList(details);
    }

    public BigDecimal getSubtotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (SaleDetail detail : details) {
            total = total.add(detail.getSubtotal());
        }
        return total;
    }

    public BigDecimal getDiscountAmount() {
        return discountPolicy.apply(this);
    }

    public BigDecimal getTaxableAmount() {
        return getSubtotal().subtract(getDiscountAmount());
    }

    public BigDecimal getTaxAmount() {
        return taxStrategy.calculateTax(this, getTaxableAmount());
    }

    public BigDecimal getTotal() {
        BigDecimal taxableAmount = getTaxableAmount();
        return taxableAmount.add(taxStrategy.calculateTax(this, taxableAmount));
    }
}