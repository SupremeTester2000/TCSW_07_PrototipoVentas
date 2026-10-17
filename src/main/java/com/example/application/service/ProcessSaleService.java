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
import java.util.regex.Pattern;
import java.util.function.Function;
import java.util.logging.Logger;

public class ProcessSaleService implements ProcessSaleUseCase {

    private static final Logger LOGGER = Logger.getLogger(ProcessSaleService.class.getName());
    private static final Pattern PRODUCT_CODE_PATTERN = Pattern.compile("^P\\d{5}$");
    private static final int MAX_PRODUCT_CODE = 99999;

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

    public void registerProduct(Product product) {
        productRepositoryPort.save(product);
    }

    @Override
    public List<Product> listProducts() {
        return productRepositoryPort.findAll();
    }

    @Override
    public void updateProduct(Product product) {
        Objects.requireNonNull(product, "El producto es obligatorio.");
        if (!productRepositoryPort.findByCode(product.getCode()).isPresent()) {
            throw new ProductNotFound(product.getCode());
        }
        productRepositoryPort.save(product);
    }

    @Override
    public String getNextProductCode() {
        int maximumCode = 0;
        for (Product product : productRepositoryPort.findAll()) {
            if (product == null || product.getCode() == null
                    || !PRODUCT_CODE_PATTERN.matcher(product.getCode()).matches()) {
                continue;
            }
            int number = Integer.parseInt(product.getCode().substring(1));
            maximumCode = Math.max(maximumCode, number);
        }

        if (maximumCode >= MAX_PRODUCT_CODE) {
            LOGGER.warning("Product code sequence reached its maximum value.");
            throw new IllegalStateException("No es posible generar un nuevo código de producto.");
        }

        return String.format(java.util.Locale.ROOT, "P%05d", maximumCode + 1);
    }

    public Sale createSale(Map<String, Integer> products) {
        return buildSale(products, new LinkedHashMap<>());
    }

    public void confirmSale(Sale sale) {
        saleRepositoryPort.save(sale);
    }

    @Override
    public Sale processSale(Map<String, Integer> requestedProducts) {
        Map<String, Product> foundProducts = new LinkedHashMap<>();

        Sale sale = buildSale(requestedProducts, foundProducts);

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

            Product product = productRepositoryPort.findByCode(code)
                    .orElseThrow(() -> new ProductNotFound(code));

            if (quantity > product.getStock()) {
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