package application.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.application.observer.StockUpdater;
import com.example.domain.observer.SaleConfirmed;
import com.example.ports.outbound.ProductRepositoryPort;

class StockUpdaterTest {

    @Test
    void updateStockOnSaleConfirmed() {
        Product product = new Product(
                "P001",
                "Laptop",
                new BigDecimal("1200.00"),
                10);

        InMemoryProductRepositoryFake repository =
                new InMemoryProductRepositoryFake();

        repository.save(product);

        Sale sale = new Sale();
        sale.addDetail(product, 3);

        StockUpdater stockUpdater =
                new StockUpdater(repository);

        stockUpdater.update(new SaleConfirmed(sale));

        assertEquals(7, product.getStock());
        assertEquals(7, repository.products.get("P001").getStock());
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
}