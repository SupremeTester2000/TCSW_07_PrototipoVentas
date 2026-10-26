package com;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import com.example.adapters.inbound.swing.controller.ProductController;
import com.example.adapters.inbound.swing.controller.CartController;
import com.example.adapters.inbound.swing.view.ProductMainFrame;
import com.example.adapters.outbound.persistence.HibernateProductRepository;
import com.example.adapters.outbound.persistence.HibernateSaleRepository;
import com.example.adapters.outbound.persistence.HibernateSessionFactory;
import com.example.adapters.outbound.persistence.HibernateSessionManager;
import com.example.adapters.outbound.persistence.HibernateTransactionAdapter;
import com.example.application.facade.SaleFacade;
import com.example.application.observer.StockUpdater;
import com.example.application.service.ProcessSaleService;
import com.example.domain.factory.DefaultSaleFactory;
import com.example.domain.factory.SaleFactory;
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
            HibernateSessionFactory persistence = HibernateSessionFactory.fromEnvironment();
            Runtime.getRuntime().addShutdownHook(new Thread(persistence::close));
            HibernateSessionManager sessionManager = new HibernateSessionManager(
                    persistence.getSessionFactory());
            ProductRepositoryPort productRepository = new HibernateProductRepository(sessionManager);
            SaleRepositoryPort saleRepository = new HibernateSaleRepository(sessionManager);
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

                SaleFacade saleFacade = new SaleFacade(
                    processSaleUseCase,
                    saleConfirmedPublisher,
                    new HibernateTransactionAdapter(sessionManager));
            ProductController productController = new ProductController(saleFacade);
            CartController cartController = new CartController(saleFacade);
            productController.loadProducts();

            ProductMainFrame frame = new ProductMainFrame(productController, cartController);
            frame.setVisible(true);
            LOGGER.info("Aplicación Swing iniciada correctamente.");
        });
    }
}