package com;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import com.example.adapters.inbound.swing.controller.ProductController;
import com.example.adapters.inbound.swing.controller.CartController;
import com.example.adapters.inbound.swing.view.ProductMainFrame;
import com.example.adapters.outbound.InMemoryProductRepository;
import com.example.adapters.outbound.InMemorySaleRepository;
import com.example.application.facade.SaleFacade;
import com.example.application.observer.StockUpdater;
import com.example.application.service.ProcessSaleService;
import com.example.domain.factory.DefaultSaleFactory;
import com.example.domain.factory.SaleFactory;
import com.example.domain.model.Product;
import com.example.domain.observer.SaleConfirmedPublisher;
import com.example.domain.strategy.FunctionalDiscountPolicies;
import com.example.domain.strategy.VatTaxStrategy;
import com.example.ports.inbound.ProcessSaleUseCase;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductRepositoryPort productRepository = new InMemoryProductRepository();
            SaleRepositoryPort saleRepository = new InMemorySaleRepository();
            SaleFactory saleFactory = new DefaultSaleFactory();

            ProcessSaleUseCase processSaleUseCase = new ProcessSaleService(
                    productRepository,
                    saleRepository,
                    saleFactory,
                    FunctionalDiscountPolicies.percentage(new BigDecimal("5")),
                    new VatTaxStrategy());

            SaleConfirmedPublisher saleConfirmedPublisher = new SaleConfirmedPublisher();
            StockUpdater stockUpdater = new StockUpdater(productRepository);
            saleConfirmedPublisher.registerObserver(stockUpdater);

            SaleFacade saleFacade = new SaleFacade(processSaleUseCase, saleConfirmedPublisher);
            ProductController productController = new ProductController(saleFacade);
            CartController cartController = new CartController(saleFacade);
            Product product = new Product("P001", "Producto de ejemplo", new BigDecimal("10.99"), 100);
            saleFacade.registerProduct(product);
            productController.loadProducts();

            ProductMainFrame frame = new ProductMainFrame(productController, cartController);
            frame.setVisible(true);
            LOGGER.info("Aplicación Swing iniciada correctamente.");
        });
    }
}