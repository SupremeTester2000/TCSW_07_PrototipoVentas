package adapters.outbound.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import com.example.adapters.outbound.persistence.HibernateProductRepository;
import com.example.adapters.outbound.persistence.HibernateSaleRepository;
import com.example.adapters.outbound.persistence.HibernateSessionFactory;
import com.example.adapters.outbound.persistence.HibernateSessionManager;
import com.example.adapters.outbound.persistence.HibernateTransactionAdapter;
import com.example.application.facade.SaleFacade;
import com.example.application.observer.StockUpdater;
import com.example.application.service.ProcessSaleService;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.observer.SaleConfirmedPublisher;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;

@EnabledIfEnvironmentVariable(named = "RUN_POSTGRES_INTEGRATION", matches = "true")
class HibernatePersistenceIntegrationTest {

    private static HibernateSessionFactory persistence;
    private static HibernateSessionManager sessionManager;
    private static HibernateProductRepository productRepository;
    private static HibernateSaleRepository saleRepository;

    private String productCode;

    @BeforeAll
    static void openSessionFactory() {
        persistence = HibernateSessionFactory.fromEnvironment();
        sessionManager = new HibernateSessionManager(persistence.getSessionFactory());
        productRepository = new HibernateProductRepository(sessionManager);
        saleRepository = new HibernateSaleRepository(sessionManager);
    }

    @AfterAll
    static void closeSessionFactory() {
        persistence.close();
    }

    @BeforeEach
    void createUniqueProductCode() {
        productCode = String.format("P%05d", ThreadLocalRandom.current().nextInt(10000, 100000));
    }

    @AfterEach
    void removeTestData() {
        List<Long> saleIds = sessionManager.inTransaction(session -> session
                .createSelectionQuery(
                        "select distinct detail.sale.id from SaleDetailEntity detail "
                                + "where detail.product.code = :code",
                        Long.class)
                .setParameter("code", productCode)
                .getResultList());

        sessionManager.inTransaction(session -> {
            session.createMutationQuery(
                    "delete from SaleDetailEntity detail where detail.product.code = :code")
                    .setParameter("code", productCode)
                    .executeUpdate();
            if (!saleIds.isEmpty()) {
                session.createMutationQuery("delete from SaleEntity sale where sale.id in :ids")
                        .setParameterList("ids", saleIds)
                        .executeUpdate();
            }
            session.createMutationQuery("delete from ProductEntity product where product.code = :code")
                    .setParameter("code", productCode)
                    .executeUpdate();
            return null;
        });
    }

    @Test
    void persists_sale_and_stock_atomically_and_rolls_back_on_observer_failure() {
        Product initialProduct = new Product(
                productCode,
                "Integration product",
                new BigDecimal("12.50"),
                8);
        productRepository.save(initialProduct);
        assertEquals(productCode, productRepository.findByCode(productCode).get().getCode());

        SaleFacade failingFacade = createFacade(true);
        Map<String, Integer> saleRequest = Map.of(productCode, 2);
        assertThrows(IllegalStateException.class, () -> failingFacade.processSale(saleRequest));
        assertEquals(8, productRepository.findByCode(productCode).get().getStock());
        assertEquals(0, countSalesForProduct());

        SaleFacade successfulFacade = createFacade(false);
        Sale sale = successfulFacade.processSale(saleRequest);

        assertEquals(6, productRepository.findByCode(productCode).get().getStock());
        assertEquals(new BigDecimal("25.00"), sale.getSubtotal());
        assertEquals(1, countSalesForProduct());
        assertEquals(1, countDetailsForProduct());
        assertEquals(new BigDecimal("12.50"), capturedPriceForProduct());

        productRepository.save(new Product(
                productCode,
                "Integration product",
                new BigDecimal("20.00"),
                6));
        assertEquals(new BigDecimal("12.50"), capturedPriceForProduct());
    }

    private SaleFacade createFacade(boolean failAfterStockUpdate) {
        ProductRepositoryPort products = productRepository;
        SaleRepositoryPort sales = saleRepository;
        ProcessSaleService service = new ProcessSaleService(products, sales);
        SaleConfirmedPublisher publisher = new SaleConfirmedPublisher();
        publisher.registerObserver(new StockUpdater(products));
        if (failAfterStockUpdate) {
            publisher.registerObserver(event -> {
                throw new IllegalStateException("Falla inducida para comprobar rollback.");
            });
        }
        return new SaleFacade(service, publisher, new HibernateTransactionAdapter(sessionManager));
    }

    private long countSalesForProduct() {
        return sessionManager.inTransaction(session -> session
                .createSelectionQuery(
                        "select count(distinct detail.sale.id) from SaleDetailEntity detail "
                                + "where detail.product.code = :code",
                        Long.class)
                .setParameter("code", productCode)
                .getSingleResult());
    }

    private long countDetailsForProduct() {
        return sessionManager.inTransaction(session -> session
                .createSelectionQuery(
                        "select count(detail) from SaleDetailEntity detail "
                                + "where detail.product.code = :code",
                        Long.class)
                .setParameter("code", productCode)
                .getSingleResult());
    }

    private BigDecimal capturedPriceForProduct() {
        return sessionManager.inTransaction(session -> session
                .createSelectionQuery(
                        "select detail.capturedPrice from SaleDetailEntity detail "
                                + "where detail.product.code = :code",
                        BigDecimal.class)
                .setParameter("code", productCode)
                .getSingleResult());
    }
}