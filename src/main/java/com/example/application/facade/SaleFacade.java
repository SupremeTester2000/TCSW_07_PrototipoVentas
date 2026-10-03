package com.example.application.facade;

import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.observer.SaleConfirmedPublisher;
import com.example.ports.inbound.ProcessSaleUseCase;
import com.example.ports.inbound.SaleFacadePort;

public class SaleFacade implements SaleFacadePort {

    private final ProcessSaleUseCase processSaleUseCase;
    private final SaleConfirmedPublisher saleConfirmedPublisher;

    public SaleFacade(ProcessSaleUseCase processSaleUseCase) {
        this(processSaleUseCase, new SaleConfirmedPublisher());
    }

    public SaleFacade(
            ProcessSaleUseCase processSaleUseCase,
            SaleConfirmedPublisher saleConfirmedPublisher) {

        if (processSaleUseCase == null) {
            throw new IllegalArgumentException(
                    "El caso de uso no puede ser nulo.");
        }

        if (saleConfirmedPublisher == null) {
            throw new IllegalArgumentException(
                    "El emisor de venta confirmada no puede ser nulo.");
        }

        this.processSaleUseCase = processSaleUseCase;
        this.saleConfirmedPublisher = saleConfirmedPublisher;
    }

    @Override
    public Sale processSale(Map<String, Integer> products) {
        Sale sale = processSaleUseCase.processSale(products);

        saleConfirmedPublisher.publishSaleConfirmed(sale);

        return sale;
    }

    public void registerProduct(Product product) {
        processSaleUseCase.registerProduct(product);
    }

    public void confirmSale(Sale sale) {
        processSaleUseCase.confirmSale(sale);
    }
}