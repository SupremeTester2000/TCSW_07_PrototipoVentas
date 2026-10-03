package com.example.domain.model;

import java.math.BigDecimal;

public class Product {

    private String code;
    private String name;
    private BigDecimal price;
    private int stock;

    public Product(String code, String name, BigDecimal price, int stock) {
        setCode(code);
        setName(name);
        setPrice(price);
        setStock(stock);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío");
        }
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
    if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("El precio no puede ser nulo o negativo");
    }
    this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("La existencia no puede ser negativa");
        }
        this.stock = stock;
    }
}
