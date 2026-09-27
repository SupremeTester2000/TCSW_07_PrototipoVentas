package com.example.application.facade;

import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.ports.inbound.ProcessSaleUseCase;

public class SaleFacade {

    private final ProcessSaleUseCase processSaleUseCase;

    public SaleFacade(ProcessSaleUseCase processSaleUseCase) {
        if (processSaleUseCase == null) {
            throw new IllegalArgumentException("El caso de uso no puede ser nulo.");
        }
        this.processSaleUseCase = processSaleUseCase;
    }

    public Sale procesarVenta(Map<String, Integer> productos) {
        Sale sale = processSaleUseCase.processSale(productos);

        // integrar estrategia de descuento.
        //disparar notificación de venta confirmada.

        return sale;
    }

    public Sale processSale(Map<String, Integer> productos) {
        return procesarVenta(productos);
    }

    public void registrarProducto(Product product) {
        processSaleUseCase.registrarProducto(product);
    }

    public void confirmarVenta(Sale sale) {
        processSaleUseCase.confirmarVenta(sale);
    }
}
