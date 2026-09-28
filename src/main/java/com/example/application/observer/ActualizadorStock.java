package com.example.application.observer;

import java.util.Objects;

import com.example.domain.model.Product;
import com.example.domain.model.SaleDetail;
import com.example.domain.observer.ObservadorVenta;
import com.example.domain.observer.VentaConfirmada;
import com.example.ports.outbound.ProductRepositoryPort;

public class ActualizadorStock implements ObservadorVenta {

    private final ProductRepositoryPort productRepositoryPort;

    public ActualizadorStock(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = Objects.requireNonNull(
                productRepositoryPort,
                "El repositorio de productos es obligatorio.");
    }

    @Override
    public void actualizar(VentaConfirmada evento) {
        Objects.requireNonNull(
                evento,
                "El evento de venta confirmada es obligatorio.");

        for (SaleDetail detalle : evento.getVenta().getDetalles()) {
            Product product = detalle.getProducto();
            int nuevaExistencia =
                    product.getExistencia() - detalle.getCantidad();

            if (nuevaExistencia < 0) {
                throw new IllegalArgumentException(
                        "La existencia del producto no puede ser negativa.");
            }

            product.setExistencia(nuevaExistencia);
            productRepositoryPort.save(product);
        }
    }
}