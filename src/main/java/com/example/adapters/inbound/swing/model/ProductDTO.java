package com.example.adapters.inbound.swing.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.domain.model.Product;

public final class ProductDTO {

    private final String code;
    private final String name;
    private final BigDecimal price;
    private final int stock;

    public ProductDTO(String code, String name, BigDecimal price, int stock) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static ProductDTO fromProduct(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductDTO(
                product.getCode(),
                product.getName(),
                product.getPrice(),
                product.getStock());
    }

    public static List<ProductDTO> fromProducts(List<Product> products) {
        List<ProductDTO> dtos = new ArrayList<>();
        if (products == null) {
            return dtos;
        }
        for (Product product : products) {
            ProductDTO dto = fromProduct(product);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public Product toProduct() {
        return new Product(code, name, price, stock);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ProductDTO)) {
            return false;
        }
        ProductDTO other = (ProductDTO) obj;
        return Objects.equals(code, other.code)
                && Objects.equals(name, other.name)
                && Objects.equals(price, other.price)
                && stock == other.stock;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, price, stock);
    }
}
