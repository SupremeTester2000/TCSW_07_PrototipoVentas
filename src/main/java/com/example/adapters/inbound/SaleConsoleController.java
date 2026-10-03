package com.example.adapters.inbound;

import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.ports.inbound.SaleFacadePort;

public class SaleConsoleController {

    private final SaleFacadePort saleFacade;

    public SaleConsoleController(SaleFacadePort saleFacade) {
        if (saleFacade == null) {
            throw new IllegalArgumentException("La fachada no puede ser nula.");
        }
        this.saleFacade = saleFacade;
    }

    public void registerProduct(Product product) {
        saleFacade.registerProduct(product);
    }

    public Sale createSale(Map<String, Integer> products) {
        return saleFacade.processSale(products);
    }

    public void confirmSale(Sale sale) {
        saleFacade.confirmSale(sale);
    }
}
