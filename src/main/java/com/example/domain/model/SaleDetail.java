package com.example.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Objeto de Valor que representa una línea dentro de la venta.
 * Es inmutable y se valida contra las existencias del producto.
 */

public final class SaleDetail {

    private final Product product;
    private final int quantity;
    private final BigDecimal capturedPrice;

    public SaleDetail(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (quantity > product.getStock()) {
            throw new IllegalArgumentException("La cantidad solicitada supera las existencias disponibles.");
        }

        this.product = product;
        this.quantity = quantity;
        this.capturedPrice = product.getPrice();
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getCapturedPrice() {
        return capturedPrice;
    }

    public BigDecimal getSubtotal() {
        return capturedPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleDetail match = (SaleDetail) o;
        return quantity == match.quantity
                && Objects.equals(capturedPrice, match.capturedPrice)
                && Objects.equals(product, match.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, capturedPrice);
    }
}