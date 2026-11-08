package com.example.config;

import java.math.BigDecimal;

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
import com.example.ports.inbound.SaleFacadePort;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;
import com.example.ports.outbound.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class SpringConfiguration {

    @Bean(destroyMethod = "close")
    public HibernateSessionFactory persistence(Environment environment) {
        return HibernateSessionFactory.fromEnvironment(environment);
    }

    @Bean
    public HibernateSessionManager sessionManager(HibernateSessionFactory persistence) {
        return new HibernateSessionManager(persistence.getSessionFactory());
    }

    @Bean
    public ProductRepositoryPort productRepository(HibernateSessionManager sessionManager) {
        return new HibernateProductRepository(sessionManager);
    }

    @Bean
    public SaleRepositoryPort saleRepository(HibernateSessionManager sessionManager) {
        return new HibernateSaleRepository(sessionManager);
    }

    @Bean
    public TransactionPort transactionPort(HibernateSessionManager sessionManager) {
        return new HibernateTransactionAdapter(sessionManager);
    }

    @Bean
    public SaleFactory saleFactory() {
        return new DefaultSaleFactory();
    }

    @Bean
    public ProcessSaleUseCase processSaleUseCase(
            ProductRepositoryPort productRepository,
            SaleRepositoryPort saleRepository,
            SaleFactory saleFactory) {
        return new ProcessSaleService(
                productRepository,
                saleRepository,
                saleFactory,
                FunctionalDiscountPolicies.percentage(new BigDecimal("5")),
                new VatTaxStrategy());
    }

    @Bean
    public StockUpdater stockUpdater(ProductRepositoryPort productRepository) {
        return new StockUpdater(productRepository);
    }

    @Bean
    public SaleConfirmedPublisher saleConfirmedPublisher(StockUpdater stockUpdater) {
        SaleConfirmedPublisher publisher = new SaleConfirmedPublisher();
        publisher.registerObserver(stockUpdater);
        return publisher;
    }

    @Bean
    public SaleFacadePort saleFacadePort(
            ProcessSaleUseCase processSaleUseCase,
            SaleConfirmedPublisher saleConfirmedPublisher,
            TransactionPort transactionPort) {
        return new SaleFacade(processSaleUseCase, saleConfirmedPublisher, transactionPort);
    }
}
