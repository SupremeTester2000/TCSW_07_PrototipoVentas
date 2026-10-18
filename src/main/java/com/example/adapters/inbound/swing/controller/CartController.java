package com.example.adapters.inbound.swing.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.adapters.inbound.swing.model.CartItemDTO;
import com.example.adapters.inbound.swing.model.CartTableModel;
import com.example.adapters.inbound.swing.model.ProductDTO;
import com.example.adapters.inbound.swing.model.SaleDTO;
import com.example.adapters.inbound.swing.model.SaleRequestDTO;
import com.example.ports.inbound.SaleFacadePort;

public class CartController {

    private final SaleFacadePort saleFacade;
    private final CartTableModel tableModel;
    private final List<CartItemDTO> items = new ArrayList<>();

    public CartController(SaleFacadePort saleFacade) {
        this(saleFacade, new CartTableModel());
    }

    public CartController(SaleFacadePort saleFacade, CartTableModel tableModel) {
        this.saleFacade = Objects.requireNonNull(saleFacade, "La fachada no puede ser nula.");
        this.tableModel = tableModel == null ? new CartTableModel() : tableModel;
    }

    public CartTableModel getTableModel() {
        return tableModel;
    }

    public List<ProductDTO> loadProducts() {
        return ProductDTO.fromProducts(saleFacade.listProducts());
    }

    public void addProduct(ProductDTO product, int quantity) {
        CartItemDTO requested = new CartItemDTO(product, quantity);
        int existingIndex = findItem(requested.getProductCode());
        int combinedQuantity = quantity + (existingIndex < 0 ? 0 : items.get(existingIndex).getQuantity());
        CartItemDTO combined = new CartItemDTO(product, combinedQuantity);
        if (existingIndex < 0) {
            items.add(combined);
        } else {
            items.set(existingIndex, combined);
        }
        refreshTable();
    }

    public void updateQuantity(int row, int quantity) {
        CartItemDTO existing = getItem(row);
        ProductDTO product = findProduct(existing.getProductCode());
        items.set(row, new CartItemDTO(product, quantity));
        refreshTable();
    }

    public void removeProduct(int row) {
        getItem(row);
        items.remove(row);
        refreshTable();
    }

    public SaleRequestDTO buildRequest() {
        Map<String, Integer> quantities = new LinkedHashMap<>();
        for (CartItemDTO item : items) {
            quantities.put(item.getProductCode(), item.getQuantity());
        }
        return new SaleRequestDTO(quantities);
    }

    public SaleDTO previewSale() {
        return previewSale(buildRequest());
    }

    public SaleDTO previewSale(SaleRequestDTO request) {
        Objects.requireNonNull(request, "La solicitud de venta es obligatoria.");
        return SaleDTO.fromSale(saleFacade.createSale(request.getQuantitiesByProductCode()));
    }

    public SaleDTO processSale(SaleRequestDTO request) {
        Objects.requireNonNull(request, "La solicitud de venta es obligatoria.");
        return SaleDTO.fromSale(saleFacade.processSale(request.getQuantitiesByProductCode()));
    }

    public void clearCart() {
        items.clear();
        refreshTable();
    }

    private CartItemDTO getItem(int row) {
        if (row < 0 || row >= items.size()) {
            throw new IllegalArgumentException("Seleccione un producto del carrito.");
        }
        return items.get(row);
    }

    private int findItem(String code) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProductCode().equals(code)) {
                return i;
            }
        }
        return -1;
    }

    private ProductDTO findProduct(String code) {
        for (ProductDTO product : loadProducts()) {
            if (product.getCode().equals(code)) {
                return product;
            }
        }
        throw new IllegalArgumentException("El producto ya no está disponible en el catálogo.");
    }

    private void refreshTable() {
        tableModel.setItems(items);
    }
}