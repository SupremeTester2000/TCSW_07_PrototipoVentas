package com.example.adapters.outbound.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SaleDetailId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "line_number")
    private short lineNumber;

    protected SaleDetailId() {
    }

    public SaleDetailId(Long saleId, short lineNumber) {
        this.saleId = saleId;
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SaleDetailId)) {
            return false;
        }
        SaleDetailId that = (SaleDetailId) other;
        return lineNumber == that.lineNumber && Objects.equals(saleId, that.saleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleId, lineNumber);
    }
}