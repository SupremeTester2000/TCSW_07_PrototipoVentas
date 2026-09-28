package com.example.application.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.domain.exception.ProductNotFound;
import com.example.domain.factory.DefaultSaleFactory;
import com.example.domain.factory.SaleFactory;
import com.example.domain.strategy.FixedDiscount;
import com.example.domain.strategy.TaxStrategy;
import com.example.domain.strategy.VatTaxStrategy;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.model.SaleDetail;
import com.example.ports.inbound.ProcessSaleUseCase;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

public class ProcessSaleService implements ProcessSaleUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final SaleRepositoryPort saleRepositoryPort;
    private final SaleFactory saleFactory;
    private final Function<Sale, BigDecimal> discountPolicy;
    private final TaxStrategy taxStrategy;

    public ProcessSaleService(
            ProductRepositoryPort productRepositoryPort,
            SaleRepositoryPort saleRepositoryPort) {
        this(productRepositoryPort, saleRepositoryPort, new DefaultSaleFactory(),
                new FixedDiscount(BigDecimal.ZERO), new VatTaxStrategy());
    }

    public ProcessSaleService(
            ProductRepositoryPort productRepositoryPort,
            SaleRepositoryPort saleRepositoryPort,
            SaleFactory saleFactory) {
        this(productRepositoryPort, saleRepositoryPort, saleFactory,
                new FixedDiscount(BigDecimal.ZERO), new VatTaxStrategy());
    }

    public ProcessSaleService(
            ProductRepositoryPort productRepositoryPort,
            SaleRepositoryPort saleRepositoryPort,
            Function<Sale, BigDecimal> discountPolicy,
            TaxStrategy taxStrategy) {
        this(productRepositoryPort, saleRepositoryPort, new DefaultSaleFactory(),
                discountPolicy, taxStrategy);
    }

    public ProcessSaleService(
            ProductRepositoryPort productRepositoryPort,
            SaleRepositoryPort saleRepositoryPort,
            SaleFactory saleFactory,
            Function<Sale, BigDecimal> discountPolicy,
            TaxStrategy taxStrategy) {
        this.productRepositoryPort = Objects.requireNonNull(
                productRepositoryPort,
                "El repositorio de productos es obligatorio.");
        this.saleRepositoryPort = Objects.requireNonNull(
                saleRepositoryPort,
                "El repositorio de ventas es obligatorio.");
        this.saleFactory = Objects.requireNonNull(
                saleFactory,
                "La fábrica de ventas es obligatoria.");
        this.discountPolicy = Objects.requireNonNull(
                discountPolicy,
                "La política de descuento es obligatoria.");
        this.taxStrategy = Objects.requireNonNull(
                taxStrategy,
                "La estrategia de impuesto es obligatoria.");
    }

    public void registrarProducto(Product product) {
        productRepositoryPort.save(product);
    }

    public Sale crearVenta(Map<String, Integer> productos) {
        return buildSale(productos, new LinkedHashMap<>());
    }

    public void confirmarVenta(Sale sale) {
        saleRepositoryPort.save(sale);
    }

    @Override
    public Sale processSale(Map<String, Integer> productosSolicitados) {
        Map<String, Product> productosEncontrados = new LinkedHashMap<>();

        Sale sale = buildSale(productosSolicitados, productosEncontrados);

        saleRepositoryPort.save(sale);

        return sale;
    }

    private Sale buildSale(
            Map<String, Integer> products,
            Map<String, Product> foundProducts) {

        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException(
                    "La venta debe contener al menos un producto.");
        }

        List<SaleDetail> details = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : products.entrySet()) {
            Integer quantity = entry.getValue();

            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor a cero.");
            }

            String code = entry.getKey();

            Product product = productRepositoryPort.findByCodigo(code)
                    .orElseThrow(() -> new ProductNotFound(code));

            if (quantity > product.getExistencia()) {
                throw new IllegalArgumentException(
                        "La cantidad solicitada supera las existencias disponibles.");
            }

            details.add(saleFactory.createSaleDetail(product, quantity));
            foundProducts.put(code, product);
        }

        return saleFactory.createSale(
                details,
                discountPolicy,
                taxStrategy);
    }
}