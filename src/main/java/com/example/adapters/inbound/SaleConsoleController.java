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

    public void registrarProducto(Product product) {
        saleFacade.registrarProducto(product);
    }

    public Sale crearVenta(Map<String, Integer> productos) {
        return saleFacade.procesarVenta(productos);
    }

    public Sale procesarVenta(Map<String, Integer> productos) {
        return saleFacade.procesarVenta(productos);
    }

    public void confirmarVenta(Sale sale) {
        saleFacade.confirmarVenta(sale);
    }
}
