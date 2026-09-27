package com.example.application.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.domain.exception.ProductNotFound;
import com.example.domain.factory.DefaultSaleFactory;
import com.example.domain.factory.SaleFactory;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.model.SaleDetail;
import com.example.ports.inbound.ProcessSaleUseCase;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;

public class ProcessSaleService implements ProcessSaleUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final SaleRepositoryPort saleRepositoryPort;
    private final SaleFactory saleFactory;

    public ProcessSaleService(ProductRepositoryPort productRepositoryPort, SaleRepositoryPort saleRepositoryPort) {
        this(productRepositoryPort, saleRepositoryPort, new DefaultSaleFactory());
    }

    public ProcessSaleService(ProductRepositoryPort productRepositoryPort, SaleRepositoryPort saleRepositoryPort, SaleFactory saleFactory) {
        if (productRepositoryPort == null) {
            throw new IllegalArgumentException("El repositorio de productos no puede ser nulo.");
        }
        if (saleRepositoryPort == null) {
            throw new IllegalArgumentException("El repositorio de ventas no puede ser nulo.");
        }
        if (saleFactory == null) {
            throw new IllegalArgumentException("La fábrica de ventas no puede ser nula.");
        }
        
        this.productRepositoryPort = productRepositoryPort;
        this.saleRepositoryPort = saleRepositoryPort;
        this.saleFactory = saleFactory;
    }

    public void registrarProducto(Product product) {
        productRepositoryPort.save(product);
    }

    public Sale crearVenta(Map<String, Integer> productos) {
        return processSale(productos);
    }

    public void confirmarVenta(Sale sale) {
        saleRepositoryPort.save(sale);
    }

    @Override
    public Sale processSale(Map<String, Integer> productosSolicitados) {
        if (productosSolicitados == null || productosSolicitados.isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }

        List<SaleDetail> detalles = new ArrayList<>();
        Map<String, Product> productosEncontrados = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : productosSolicitados.entrySet()) {
            String codigo = entry.getKey();
            Integer cantidad = entry.getValue();

            if (cantidad == null || cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }

            Product product = productRepositoryPort.findByCodigo(codigo)
                    .orElseThrow(() -> new ProductNotFound(codigo));

            if (cantidad > product.getExistencia()) {
                throw new IllegalArgumentException("La cantidad solicitada supera las existencias disponibles.");
            }

            SaleDetail detalle = saleFactory.createSaleDetail(product, cantidad);
            detalles.add(detalle);
            productosEncontrados.put(codigo, product);
        }

        Sale sale = saleFactory.createSale(detalles);

        for (Map.Entry<String, Integer> entry : productosSolicitados.entrySet()) {
            String codigo = entry.getKey();
            int cantidad = entry.getValue();
            Product product = productosEncontrados.get(codigo);
            product.setExistencia(product.getExistencia() - cantidad);
            productRepositoryPort.save(product);
        }

        saleRepositoryPort.save(sale);
        return sale;
    }
}