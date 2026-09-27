package com.example.domain.factory;

import java.math.BigDecimal;
import java.util.List;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.model.SaleDetail;

public class DefaultSaleFactory implements SaleFactory {

    @Override
    public Sale createSale(List<SaleDetail> details) {
        if (details == null) {
            throw new IllegalArgumentException("La lista de detalles no puede ser nula.");
        }
        if (details.isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un detalle.");
        }

        BigDecimal totalCalculado = BigDecimal.ZERO;
        Sale sale = new Sale();

        for (SaleDetail detail : details) {
            if (detail == null) {
                throw new IllegalArgumentException("El detalle de venta no puede ser nulo.");
            }
            if (detail.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
            if (detail.getProducto() == null) {
                throw new IllegalArgumentException("El producto asociado al detalle no puede ser nulo.");
            }
            if (detail.getPrecioCapturado() == null ||
                    detail.getPrecioCapturado().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio capturado debe ser mayor a cero.");
            }

            totalCalculado = totalCalculado.add(detail.getSubtotal());
            sale.agregarDetalle(detail);
        }

        if (sale.getTotal().compareTo(totalCalculado) != 0) {
            throw new IllegalStateException(
                    "El total de la venta no coincide con la suma de subtotales.");
        }

        return sale;
    }

    @Override
    public SaleDetail createSaleDetail(Product producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (producto.getPrecio() == null ||
                producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor a cero.");
        }
        return new SaleDetail(producto, cantidad);
    }
}