package com.example.domain.observer;

import java.util.Objects;

import com.example.domain.model.Sale;

public final class VentaConfirmada {

    private final Sale venta;

    public VentaConfirmada(Sale venta) {
        this.venta = Objects.requireNonNull(
                venta,
                "La venta confirmada es obligatoria.");
    }

    public Sale getVenta() {
        return venta;
    }
}