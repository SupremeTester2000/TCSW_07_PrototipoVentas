package com.example.application.service;

import com.example.domain.exception.ProductNotFound;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.ports.inbound.ProcessSaleUseCase;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProcessSaleService implements ProcessSaleUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final SaleRepositoryPort saleRepositoryPort;

    public ProcessSaleService(
            ProductRepositoryPort productRepositoryPort,
            SaleRepositoryPort saleRepositoryPort) {

        this.productRepositoryPort = productRepositoryPort;
        this.saleRepositoryPort = saleRepositoryPort;
    }

    public void registrarProducto(Product product) {
        productRepositoryPort.save(product);
    }

    public Sale crearVenta(Map<String, Integer> productos) {
        if (productos == null) {
            throw new IllegalArgumentException(
                    "Los productos de la venta no pueden ser nulos");
        }

        Sale sale = new Sale();

        for (Map.Entry<String, Integer> item : productos.entrySet()) {
            Product product = productRepositoryPort.findByCodigo(item.getKey())
                    .orElseThrow(() -> new ProductNotFound(item.getKey()));

            sale.agregarDetalle(product, item.getValue());
        }

        return sale;
    }

    public void confirmarVenta(Sale sale) {
        saleRepositoryPort.save(sale);
    }

    @Override
    public Sale processSale(Map<String, Integer> productosSolicitados) {

        if (productosSolicitados == null || productosSolicitados.isEmpty()) {
            throw new IllegalArgumentException(
                    "La venta debe contener al menos un producto.");
        }

        Sale sale = new Sale();

        Map<String, Product> productosEncontrados = new LinkedHashMap<>();

        // Validar toda la venta antes de modificar existencias.
        for (Map.Entry<String, Integer> entry : productosSolicitados.entrySet()) {

            String codigo = entry.getKey();
            Integer cantidad = entry.getValue();

            if (cantidad == null || cantidad <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor a cero.");
            }

            Product product = productRepositoryPort.findByCodigo(codigo)
                    .orElseThrow(() -> new ProductNotFound(codigo));

            sale.agregarDetalle(product, cantidad);

            productosEncontrados.put(codigo, product);
        }

        // Actualizar existencias después de validar toda la venta.
        for (Map.Entry<String, Integer> entry : productosSolicitados.entrySet()) {

            String codigo = entry.getKey();
            int cantidad = entry.getValue();

            Product product = productosEncontrados.get(codigo);

            product.setExistencia(product.getExistencia() - cantidad);

            productRepositoryPort.save(product);
        }

        // Guardar la venta.
        saleRepositoryPort.save(sale);

        return sale;
    }
}