package com.example.ports.inbound;

import java.util.List;
import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;

public interface SaleFacadePort {

    Sale processSale(Map<String, Integer> products);

    default Sale createSale(Map<String, Integer> products) {
        throw new UnsupportedOperationException("La previsualización de ventas no está disponible.");
    }

    void registerProduct(Product product);

    default List<Product> listProducts() {
        return java.util.Collections.emptyList();
    }

    default void updateProduct(Product product) {
        throw new UnsupportedOperationException("La actualización de productos no está disponible.");
    }

    default String getNextProductCode() {
        throw new UnsupportedOperationException("La generación de códigos no está disponible.");
    }

    void confirmSale(Sale sale);
}
