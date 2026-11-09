package com.example.adapters.inbound.rest.dto;

import java.math.BigDecimal;

public final class ProductResponse {

    private final String code;
    private final String name;
    private final BigDecimal price;
    private final int stock;

    public ProductResponse(String code, String name, BigDecimal price, int stock) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
}
