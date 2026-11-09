package com.example.adapters.inbound.rest.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.adapters.inbound.rest.dto.ProductRequest;
import com.example.adapters.inbound.rest.dto.ProductResponse;
import com.example.adapters.inbound.rest.dto.SaleResponse;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.model.SaleDetail;

public final class RestDtoMapper {

    public Product toProduct(ProductRequest request, String code) {
        Objects.requireNonNull(request, "La solicitud de producto es obligatoria.");
        return new Product(code, request.getName(), request.getPrice(), request.getStock());
    }

    public ProductResponse toProductResponse(Product product) {
        Objects.requireNonNull(product, "El producto es obligatorio.");
        return new ProductResponse(
                product.getCode(), product.getName(), product.getPrice(), product.getStock());
    }

    public List<ProductResponse> toProductResponses(List<Product> products) {
        return products.stream().map(this::toProductResponse).collect(Collectors.toList());
    }

    public SaleResponse toSaleResponse(Sale sale) {
        Objects.requireNonNull(sale, "La venta es obligatoria.");
        List<SaleResponse.Line> lines = sale.getDetails().stream()
                .map(this::toSaleLine)
                .collect(Collectors.toList());
        return new SaleResponse(
                lines, sale.getSubtotal(), sale.getDiscountAmount(),
                sale.getTaxAmount(), sale.getTotal());
    }

    private SaleResponse.Line toSaleLine(SaleDetail detail) {
        return new SaleResponse.Line(
                detail.getProduct().getCode(),
                detail.getProduct().getName(),
                detail.getQuantity(),
                detail.getCapturedPrice(),
                detail.getSubtotal());
    }
}
