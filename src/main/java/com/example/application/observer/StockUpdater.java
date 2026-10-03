package com.example.application.observer;

import java.util.Objects;

import com.example.domain.model.Product;
import com.example.domain.model.SaleDetail;
import com.example.domain.observer.SaleObserver;
import com.example.domain.observer.SaleConfirmed;
import com.example.ports.outbound.ProductRepositoryPort;

public class StockUpdater implements SaleObserver {

    private final ProductRepositoryPort productRepositoryPort;

    public StockUpdater(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = Objects.requireNonNull(
                productRepositoryPort,
                "El repositorio de productos es obligatorio.");
    }

    @Override
    public void update(SaleConfirmed event) {
        Objects.requireNonNull(
                event,
                "El evento de venta confirmada es obligatorio.");

        for (SaleDetail detail : event.getSale().getDetails()) {
            Product product = detail.getProduct();
            int newStock =
                    product.getStock() - detail.getQuantity();

            if (newStock < 0) {
                throw new IllegalArgumentException(
                        "La existencia del producto no puede ser negativa.");
            }

            product.setStock(newStock);
            productRepositoryPort.save(product);
        }
    }
}