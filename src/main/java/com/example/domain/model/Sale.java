package com.example.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sale {

    private final List<SaleDetail> detalles = new ArrayList<>();

    public void agregarDetalle(Product producto, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (cantidad > producto.getExistencia()) {
            throw new IllegalArgumentException("La cantidad solicitada supera las existencias disponibles.");
        }
        
        SaleDetail nuevoDetalle = new SaleDetail(producto, cantidad);
        detalles.add(nuevoDetalle);
    }

    public List<SaleDetail> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public BigDecimal getTotal() {
        if (detalles.isEmpty()) {   
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (SaleDetail detalle : detalles) {
            total = total.add(detalle.getSubtotal());
        }
        return total;
    }
}