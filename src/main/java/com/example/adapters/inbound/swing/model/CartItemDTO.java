package com.example.adapters.inbound.swing.model;

import java.math.BigDecimal;
import java.io.Serializable;

public final class CartItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String productCode;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final int availableStock;

    public CartItemDTO(ProductDTO product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Debe seleccionar un producto.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (quantity > product.getStock()) {
            throw new IllegalArgumentException("La cantidad supera las existencias disponibles.");
        }
        this.productCode = product.getCode();
        this.productName = product.getName();
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.availableStock = product.getStock();
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public int getAvailableStock() {
        return availableStock;
    }
}