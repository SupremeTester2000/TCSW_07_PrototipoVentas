package com.example.ports.inbound;

import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;

public interface SaleFacadePort {

    Sale processSale(Map<String, Integer> products);

    void registerProduct(Product product);

    void confirmSale(Sale sale);
}
