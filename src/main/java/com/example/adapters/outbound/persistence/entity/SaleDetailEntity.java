package com.example.adapters.outbound.persistence.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "sale_details")
public class SaleDetailEntity {

    @EmbeddedId
    private SaleDetailId id;

    @MapsId("saleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id", nullable = false)
    private SaleEntity sale;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_code", nullable = false)
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "captured_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal capturedPrice;

    protected SaleDetailEntity() {
    }

    public SaleDetailEntity(short lineNumber, ProductEntity product, int quantity,
            BigDecimal capturedPrice) {
        this.id = new SaleDetailId(null, lineNumber);
        this.product = product;
        this.quantity = quantity;
        this.capturedPrice = capturedPrice;
    }

    void setSale(SaleEntity sale) {
        this.sale = sale;
    }
}