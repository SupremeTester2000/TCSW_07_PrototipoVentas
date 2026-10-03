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

        BigDecimal calculatedTotal = BigDecimal.ZERO;
        Sale sale = new Sale();

        for (SaleDetail detail : details) {
            if (detail == null) {
                throw new IllegalArgumentException("El detalle de venta no puede ser nulo.");
            }
            if (detail.getQuantity() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
            if (detail.getProduct() == null) {
                throw new IllegalArgumentException("El producto asociado al detalle no puede ser nulo.");
            }
            if (detail.getCapturedPrice() == null ||
                    detail.getCapturedPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio capturado debe ser mayor a cero.");
            }

            calculatedTotal = calculatedTotal.add(detail.getSubtotal());
            sale.addDetail(detail);
        }

        if (sale.getSubtotal().compareTo(calculatedTotal) != 0) {
            throw new IllegalStateException(
                    "El total de la venta no coincide con la suma de subtotales.");
        }

        return sale;
    }

    @Override
    public SaleDetail createSaleDetail(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (product.getPrice() == null ||
                product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor a cero.");
        }
        return new SaleDetail(product, quantity);
    }
}