package com.example.domain.observer;

import java.util.Objects;

import com.example.domain.model.Sale;

public final class SaleConfirmed {

    private final Sale sale;

    public SaleConfirmed(Sale sale) {
        this.sale = Objects.requireNonNull(
                sale,
                "La venta confirmada es obligatoria.");
    }

    public Sale getSale() {
        return sale;
    }
}