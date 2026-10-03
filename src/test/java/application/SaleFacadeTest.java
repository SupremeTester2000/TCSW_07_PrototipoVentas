package application;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.example.application.facade.SaleFacade;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.observer.SaleConfirmedPublisher;
import com.example.ports.inbound.ProcessSaleUseCase;

class SaleFacadeTest {

    @Test
    void delegateSaleProcessingCorrectly() {
        Product product = new Product(
                "P001",
                "Laptop",
                new BigDecimal("1200.00"),
                10);

        Sale expectedSale = new Sale();
        expectedSale.addDetail(product, 1);

        final Map<String, Integer> captured = new LinkedHashMap<>();

        ProcessSaleUseCase useCase = new ProcessSaleUseCase() {
            @Override
            public void registerProduct(Product product) {
            }

            @Override
            public Sale createSale(Map<String, Integer> products) {
                return expectedSale;
            }

            @Override
            public void confirmSale(Sale sale) {
            }

            @Override
            public Sale processSale(
                    Map<String, Integer> requestedProducts) {
                captured.putAll(requestedProducts);
                return expectedSale;
            }
        };

        SaleFacade facade = new SaleFacade(useCase);

        Map<String, Integer> request = new LinkedHashMap<>();
        request.put("P001", 1);

        Sale result = facade.processAndNotifySale(request);

        assertSame(expectedSale, result);
        assertSame(1, captured.get("P001"));
    }

    @Test
    void delegateProcessSaleToProcessAndNotifySale() {
        Sale expectedSale = new Sale();

        ProcessSaleUseCase useCase = new ProcessSaleUseCase() {
            @Override
            public void registerProduct(Product product) {
            }

            @Override
            public Sale createSale(Map<String, Integer> products) {
                return expectedSale;
            }

            @Override
            public void confirmSale(Sale sale) {
            }

            @Override
            public Sale processSale(
                    Map<String, Integer> requestedProducts) {
                return expectedSale;
            }
        };

        SaleFacade facade = new SaleFacade(useCase);

        Map<String, Integer> request = new LinkedHashMap<>();
        request.put("P001", 1);

        Sale result = facade.processSale(request);

        assertSame(expectedSale, result);
    }

    @Test
    void delegateProductRegistrationCorrectly() {
        Product product = new Product(
                "P001",
                "Laptop",
                new BigDecimal("1200.00"),
                10);

        final Product[] capturedProduct = new Product[1];

        ProcessSaleUseCase useCase = new ProcessSaleUseCase() {
            @Override
            public void registerProduct(Product product) {
                capturedProduct[0] = product;
            }

            @Override
            public Sale createSale(Map<String, Integer> products) {
                return new Sale();
            }

            @Override
            public void confirmSale(Sale sale) {
            }

            @Override
            public Sale processSale(
                    Map<String, Integer> requestedProducts) {
                return new Sale();
            }
        };

        SaleFacade facade = new SaleFacade(useCase);

        facade.registerProduct(product);

        assertSame(product, capturedProduct[0]);
    }

    @Test
    void delegateSaleConfirmationCorrectly() {
        Sale sale = new Sale();

        final Sale[] capturedSale = new Sale[1];

        ProcessSaleUseCase useCase = new ProcessSaleUseCase() {
            @Override
            public void registerProduct(Product product) {
            }

            @Override
            public Sale createSale(Map<String, Integer> products) {
                return new Sale();
            }

            @Override
            public void confirmSale(Sale sale) {
                capturedSale[0] = sale;
            }

            @Override
            public Sale processSale(
                    Map<String, Integer> requestedProducts) {
                return new Sale();
            }
        };

        SaleFacade facade = new SaleFacade(useCase);

        facade.confirmSale(sale);

        assertSame(sale, capturedSale[0]);
    }

    @Test
    void rejectNullUseCase() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SaleFacade(null));
    }

    @Test
    void rejectNullPublisher() {
        ProcessSaleUseCase useCase = new ProcessSaleUseCase() {
            @Override
            public void registerProduct(Product product) {
            }

            @Override
            public Sale createSale(Map<String, Integer> products) {
                return new Sale();
            }

            @Override
            public void confirmSale(Sale sale) {
            }

            @Override
            public Sale processSale(
                    Map<String, Integer> requestedProducts) {
                return new Sale();
            }
        };

        assertThrows(
                IllegalArgumentException.class,
                () -> new SaleFacade(
                        useCase,
                        null));
    }
}