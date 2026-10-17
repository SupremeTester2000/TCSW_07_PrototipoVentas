package application;

import com.example.application.facade.SaleFacade;
import com.example.application.observer.StockUpdater;
import com.example.application.service.ProcessSaleService;
import com.example.domain.exception.ProductNotFound;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.observer.SaleConfirmedPublisher;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;

class ProcessSaleServiceTest {

    @Test
    void processSaleWithOneProduct() {

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

        ProcessSaleService service =
                new ProcessSaleService(productRepository, saleRepository);

        Map<String, Integer> requestedProducts = new LinkedHashMap<>();
        requestedProducts.put("P001", 2);

        Sale sale = service.processSale(requestedProducts);

        assertEquals(1, sale.getDetails().size());

        assertEquals(
                0,
                new BigDecimal("2784.00").compareTo(sale.getTotal()));
    }

    @Test
    void processSaleWithMultipleProducts() {

        InMemoryProductRepositoryFake productRepository =
                new InMemoryProductRepositoryFake();

        InMemorySaleRepositoryFake saleRepository =
                new InMemorySaleRepositoryFake();

        Product laptop = new Product(
                "P001",
                "Laptop",
                new BigDecimal("1200.00"),
                10);

        Product mouse = new Product(
                "P002",
                "Mouse",
                new BigDecimal("250.00"),
                5);

        productRepository.save(laptop);
        productRepository.save(mouse);

        ProcessSaleService service =
                new ProcessSaleService(productRepository, saleRepository);

        Map<String, Integer> requestedProducts = new LinkedHashMap<>();
        requestedProducts.put("P001", 2);
        requestedProducts.put("P002", 3);

        Sale sale = service.processSale(requestedProducts);

        assertEquals(2, sale.getDetails().size());

        assertEquals(
                0,
                new BigDecimal("3654.00").compareTo(sale.getTotal()));
    }

    @Test
    void throwProductNotFoundForMissingProduct() {

        InMemoryProductRepositoryFake productRepository =
                new InMemoryProductRepositoryFake();

        InMemorySaleRepositoryFake saleRepository =
                new InMemorySaleRepositoryFake();

        ProcessSaleService service =
                new ProcessSaleService(productRepository, saleRepository);

        Map<String, Integer> requestedProducts = new LinkedHashMap<>();
        requestedProducts.put("P999", 1);

        assertThrows(
                ProductNotFound.class,
                () -> service.processSale(requestedProducts));
    }

    @Test
    void rejectZeroQuantity() {

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

        ProcessSaleService service =
                new ProcessSaleService(productRepository, saleRepository);

        Map<String, Integer> requestedProducts = new LinkedHashMap<>();
        requestedProducts.put("P001", 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.processSale(requestedProducts));
    }

    @Test
    void rejectNegativeQuantity() {

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

        ProcessSaleService service =
                new ProcessSaleService(productRepository, saleRepository);

        Map<String, Integer> requestedProducts = new LinkedHashMap<>();
        requestedProducts.put("P001", -2);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.processSale(requestedProducts));
    }

    @Test
    void rejectInsufficientStock() {

        InMemoryProductRepositoryFake productRepository =
                new InMemoryProductRepositoryFake();

        InMemorySaleRepositoryFake saleRepository =
                new InMemorySaleRepositoryFake();

        Product product = new Product(
                "P001",
                "Laptop",
                new BigDecimal("1200.00"),
                3);

        productRepository.save(product);

        ProcessSaleService service =
                new ProcessSaleService(productRepository, saleRepository);

        Map<String, Integer> requestedProducts = new LinkedHashMap<>();
        requestedProducts.put("P001", 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.processSale(requestedProducts));

        assertEquals(3, product.getStock());
    }

    @Test
    void updateStockAfterSale() {

        InMemoryProductRepositoryFake productRepository =
                new InMemoryProductRepositoryFake();

        InMemorySaleRepositoryFake saleRepository =
                new InMemorySaleRepositoryFake();

        Product product = new Product(
                "P001",
                "Teclado",
                new BigDecimal("400.00"),
                8);

        productRepository.save(product);

        ProcessSaleService service =
                new ProcessSaleService(productRepository, saleRepository);

        SaleConfirmedPublisher publisher =
                new SaleConfirmedPublisher();

        StockUpdater stockUpdater =
                new StockUpdater(productRepository);

        publisher.registerObserver(stockUpdater);

        SaleFacade facade =
                new SaleFacade(
                        service,
                        publisher);

        Map<String, Integer> requestedProducts =
                new LinkedHashMap<>();

        requestedProducts.put("P001", 3);

        facade.processSale(requestedProducts);

        assertEquals(5, product.getStock());

        assertSame(
                product,
                productRepository.products.get("P001"));
    }

    @Test
    void saveSaleAndCalculateTotal() {

        InMemoryProductRepositoryFake productRepository =
                new InMemoryProductRepositoryFake();

        InMemorySaleRepositoryFake saleRepository =
                new InMemorySaleRepositoryFake();

        Product monitor = new Product(
                "P001",
                "Monitor",
                new BigDecimal("3500.00"),
                5);

        Product keyboard = new Product(
                "P002",
                "Teclado",
                new BigDecimal("400.00"),
                5);

        productRepository.save(monitor);
        productRepository.save(keyboard);

        ProcessSaleService service =
                new ProcessSaleService(productRepository, saleRepository);

        Map<String, Integer> requestedProducts = new LinkedHashMap<>();
        requestedProducts.put("P001", 1);
        requestedProducts.put("P002", 2);

        Sale sale = service.processSale(requestedProducts);

        assertEquals(
                0,
		new BigDecimal("4988.00").compareTo(sale.getTotal()));
        assertEquals(1, saleRepository.sales.size());

        assertSame(
                sale,
                saleRepository.sales.get(0));
    }

    @Test
    void generateNextProductCodeFromMaximumValidExistingCode() {
        InMemoryProductRepositoryFake products = new InMemoryProductRepositoryFake();
        products.save(new Product("P00001", "Uno", BigDecimal.ONE, 1));
        products.save(new Product("P00004", "Cuatro", BigDecimal.ONE, 1));
        products.save(new Product("legacy", "Legacy", BigDecimal.ONE, 1));

        ProcessSaleService service = new ProcessSaleService(
                products,
                new InMemorySaleRepositoryFake());

        assertEquals("P00005", service.getNextProductCode());
    }

    @Test
    void generateFirstProductCodeWhenThereAreNoValidCodes() {
        InMemoryProductRepositoryFake products = new InMemoryProductRepositoryFake();
        products.save(new Product("P001", "Legacy", BigDecimal.ONE, 1));

        ProcessSaleService service = new ProcessSaleService(
                products,
                new InMemorySaleRepositoryFake());

        assertEquals("P00001", service.getNextProductCode());
    }

    @Test
    void failWhenProductCodeSequenceIsExhausted() {
        InMemoryProductRepositoryFake products = new InMemoryProductRepositoryFake();
        products.save(new Product("P99999", "Final", BigDecimal.ONE, 1));

        ProcessSaleService service = new ProcessSaleService(
                products,
                new InMemorySaleRepositoryFake());

        assertThrows(IllegalStateException.class, service::getNextProductCode);
    }

    @Test
    void updateExistingProductWithoutChangingItsCode() {
        InMemoryProductRepositoryFake products = new InMemoryProductRepositoryFake();
        products.save(new Product("P00001", "Original", BigDecimal.ONE, 1));

        ProcessSaleService service = new ProcessSaleService(
                products,
                new InMemorySaleRepositoryFake());
        Product updated = new Product("P00001", "Actualizado", new BigDecimal("50.99"), 20);
        service.updateProduct(updated);

        Product stored = products.findByCode("P00001").get();
        assertEquals("P00001", stored.getCode());
        assertEquals("Actualizado", stored.getName());
        assertEquals(0, new BigDecimal("50.99").compareTo(stored.getPrice()));
        assertEquals(20, stored.getStock());
    }

    @Test
    void updatingMissingProductDoesNotRegisterIt() {
        InMemoryProductRepositoryFake products = new InMemoryProductRepositoryFake();
        ProcessSaleService service = new ProcessSaleService(
                products,
                new InMemorySaleRepositoryFake());

        assertThrows(
                ProductNotFound.class,
                () -> service.updateProduct(new Product(
                        "P00001", "No existente", BigDecimal.ONE, 1)));
        assertEquals(0, products.findAll().size());
    }

    private static class InMemoryProductRepositoryFake
            implements ProductRepositoryPort {

        private final Map<String, Product> products =
                new LinkedHashMap<>();

        @Override
        public Optional<Product> findByCode(String code) {
            return Optional.ofNullable(products.get(code));
        }

        @Override
        public void save(Product product) {
            products.put(product.getCode(), product);
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