package com.example.ports.inbound;

import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;

public interface ProcessSaleUseCase {

    void registerProduct(Product product);

    Sale createSale(Map<String, Integer> products);

    void confirmSale(Sale sale);

    Sale processSale(Map<String, Integer> requestedProducts);
}
