package com.example.adapters.inbound.swing.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.example.adapters.inbound.swing.model.ProductDTO;
import com.example.adapters.inbound.swing.model.ProductTableModel;
import com.example.domain.model.Product;
import com.example.ports.inbound.SaleFacadePort;

public class ProductController {

    private final SaleFacadePort saleFacade;
    private final ProductTableModel tableModel;

    public ProductController(SaleFacadePort saleFacade) {
        this(saleFacade, new ProductTableModel());
    }

    public ProductController(SaleFacadePort saleFacade, ProductTableModel tableModel) {
        this.saleFacade = Objects.requireNonNull(saleFacade, "La fachada no puede ser nula.");
        this.tableModel = tableModel == null ? new ProductTableModel() : tableModel;
    }

    public ProductTableModel getTableModel() {
        return tableModel;
    }

    public List<ProductDTO> loadProducts() {
        List<ProductDTO> products = ProductDTO.fromProducts(saleFacade.listProducts());
        tableModel.setProducts(products);
        return products;
    }

    public String getNextProductCode() {
        return saleFacade.getNextProductCode();
    }

    public void registerProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        Product product = productDTO.toProduct();
        saleFacade.registerProduct(product);
        loadProducts();
    }

    public void updateProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        saleFacade.updateProduct(productDTO.toProduct());
        loadProducts();
    }

    public ProductDTO validateAndBuildProduct(String code, String name, String priceText, String stockText) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        BigDecimal price;
        if (priceText == null || priceText.trim().isEmpty()) {
            throw new IllegalArgumentException("El precio debe ser un número válido.");
        }
        try {
            price = new BigDecimal(priceText.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El precio debe ser un número válido.", ex);
        }
        int stock;
        if (stockText == null || stockText.trim().isEmpty()) {
            throw new IllegalArgumentException("La existencia debe ser un número entero válido.");
        }
        try {
            stock = Integer.parseInt(stockText.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("La existencia debe ser un número entero válido.", ex);
        }
        return new ProductDTO(code.trim(), name.trim(), price, stock);
    }
}
