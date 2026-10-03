package com.example.adapters.inbound;

import java.util.Map;

import com.example.application.facade.SaleFacade;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;

public class SaleConsoleController {

    private final SaleFacade saleFacade;

    public SaleConsoleController(SaleFacade saleFacade) {
        if (saleFacade == null) {
            throw new IllegalArgumentException("La fachada no puede ser nula.");
        }
        this.saleFacade = saleFacade;
    }

    public void registerProduct(Product product) {
        saleFacade.registerProduct(product);
    }

    public Sale createSale(Map<String, Integer> products) {
        return saleFacade.processAndNotifySale(products);
    }

    public Sale processAndNotifySale(Map<String, Integer> products) {
        return saleFacade.processAndNotifySale(products);
    }

    public void confirmSale(Sale sale) {
        saleFacade.confirmSale(sale);
    }
}
