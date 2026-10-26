package com.example.application.facade;

import java.util.List;
import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.observer.SaleConfirmedPublisher;
import com.example.ports.inbound.ProcessSaleUseCase;
import com.example.ports.inbound.SaleFacadePort;
import com.example.ports.outbound.TransactionPort;

public class SaleFacade implements SaleFacadePort {

    private final ProcessSaleUseCase processSaleUseCase;
    private final SaleConfirmedPublisher saleConfirmedPublisher;
    private final TransactionPort transactionPort;

    public SaleFacade(ProcessSaleUseCase processSaleUseCase) {
        this(processSaleUseCase, new SaleConfirmedPublisher(), null);
    }

    public SaleFacade(
            ProcessSaleUseCase processSaleUseCase,
            SaleConfirmedPublisher saleConfirmedPublisher) {
        this(processSaleUseCase, saleConfirmedPublisher, null);
    }

    public SaleFacade(
            ProcessSaleUseCase processSaleUseCase,
            SaleConfirmedPublisher saleConfirmedPublisher,
            TransactionPort transactionPort) {

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
        this.transactionPort = transactionPort;
    }

    @Override
    public Sale processSale(Map<String, Integer> products) {
        java.util.function.Supplier<Sale> operation = () -> {
            Sale sale = processSaleUseCase.processSale(products);
            saleConfirmedPublisher.publishSaleConfirmed(sale);
            return sale;
        };

        return transactionPort == null ? operation.get() : transactionPort.execute(operation);
    }

    @Override
    public Sale createSale(Map<String, Integer> products) {
        return processSaleUseCase.createSale(products);
    }

    public void registerProduct(Product product) {
        processSaleUseCase.registerProduct(product);
    }

    @Override
    public List<Product> listProducts() {
        return processSaleUseCase.listProducts();
    }

    @Override
    public void updateProduct(Product product) {
        processSaleUseCase.updateProduct(product);
    }

    @Override
    public String getNextProductCode() {
        return processSaleUseCase.getNextProductCode();
    }

    public void confirmSale(Sale sale) {
        processSaleUseCase.confirmSale(sale);
    }
}