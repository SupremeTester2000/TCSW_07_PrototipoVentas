package com.example.adapters.outbound.persistence.entity;

import java.math.BigDecimal;

import com.example.domain.model.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @Column(name = "code", length = 6, nullable = false)
    private String code;

    @Column(name = "name", length = 120, nullable = false)
    private String name;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private int stock;

    protected ProductEntity() {
    }

    private ProductEntity(String code, String name, BigDecimal price, int stock) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static ProductEntity fromDomain(Product product) {
        return new ProductEntity(
                product.getCode(),
                product.getName(),
                product.getPrice(),
                product.getStock());
    }

    public Product toDomain() {
        return new Product(code, name, price, stock);
    }
}