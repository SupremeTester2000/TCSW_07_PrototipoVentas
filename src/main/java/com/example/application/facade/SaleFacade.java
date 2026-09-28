package com.example.application.facade;

import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.observer.EmisorVentaConfirmada;
import com.example.ports.inbound.ProcessSaleUseCase;

public class SaleFacade {

    private final ProcessSaleUseCase processSaleUseCase;
    private final EmisorVentaConfirmada emisorVentaConfirmada;

    public SaleFacade(ProcessSaleUseCase processSaleUseCase) {
        this(processSaleUseCase, new EmisorVentaConfirmada());
    }

    public SaleFacade(
            ProcessSaleUseCase processSaleUseCase,
            EmisorVentaConfirmada emisorVentaConfirmada) {

        if (processSaleUseCase == null) {
            throw new IllegalArgumentException(
                    "El caso de uso no puede ser nulo.");
        }

        if (emisorVentaConfirmada == null) {
            throw new IllegalArgumentException(
                    "El emisor de venta confirmada no puede ser nulo.");
        }

        this.processSaleUseCase = processSaleUseCase;
        this.emisorVentaConfirmada = emisorVentaConfirmada;
    }

    public Sale procesarVenta(Map<String, Integer> productos) {
        Sale sale = processSaleUseCase.processSale(productos);

        emisorVentaConfirmada.notificarVentaConfirmada(sale);

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