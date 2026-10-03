package application.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.application.facade.SaleFacade;
import com.example.application.observer.StockUpdater;
import com.example.application.service.ProcessSaleService;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.observer.SaleConfirmedPublisher;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;

class SaleFacadeObserverTest {

    @Test
    void processSaleAndNotifyStockUpdater() {

        InMemoryProductRepositoryFake productRepository =
                new InMemoryProductRepositoryFake();

        InMemorySaleRepositoryFake saleRepository =
                new InMemorySaleRepositoryFake();

        Product product = new Product(
                "P001",
                "Laptop",
                new BigDecimal("1200.00"),
                10);

        productRepository.save(product);

        ProcessSaleService processSaleService =
                new ProcessSaleService(
                        productRepository,
                        saleRepository);

        SaleConfirmedPublisher publisher =
                new SaleConfirmedPublisher();

        StockUpdater stockUpdater =
                new StockUpdater(productRepository);

        publisher.registerObserver(stockUpdater);

        SaleFacade facade =
                new SaleFacade(
                        processSaleService,
                        publisher);

        Map<String, Integer> products =
                new LinkedHashMap<>();

        products.put("P001", 2);

        Sale sale = facade.processAndNotifySale(products);

        assertEquals(1, sale.getDetails().size());
        assertEquals(
                0,
                new BigDecimal("2784.00").compareTo(
                        sale.getTotal()));

        assertEquals(8, product.getStock());
        assertEquals(1, saleRepository.sales.size());
    }

    private static class InMemoryProductRepositoryFake
            implements ProductRepositoryPort {

        private final Map<String, Product> products =
                new LinkedHashMap<>();

        @Override
        public void save(Product product) {
            products.put(product.getCode(), product);
        }

        @Override
        public Optional<Product> findByCode(String code) {
            return Optional.ofNullable(products.get(code));
        }

        @Override
        public List<Product> findAll() {
            return new ArrayList<>(products.values());
        }
    }

    private static class InMemorySaleRepositoryFake
            implements SaleRepositoryPort {

        private final List<Sale> sales =
                new ArrayList<>();

        @Override
        public void save(Sale sale) {
            sales.add(sale);
        }
    }
}