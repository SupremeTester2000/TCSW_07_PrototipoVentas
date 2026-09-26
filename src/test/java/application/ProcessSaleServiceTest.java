package application;

import com.example.application.service.ProcessSaleService;
import com.example.domain.exception.ProductNotFound;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
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
    void procesaVentaConUnProducto() {

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

        assertEquals(1, sale.getDetalles().size());

        assertEquals(
                0,
                new BigDecimal("2400.00").compareTo(sale.getTotal()));
    }

    @Test
    void procesaVentaConVariosProductos() {

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

        assertEquals(2, sale.getDetalles().size());

        assertEquals(
                0,
                new BigDecimal("3150.00").compareTo(sale.getTotal()));
    }

    @Test
    void productoInexistenteLanzaProductNotFound() {

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
    void cantidadCeroEsRechazada() {

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
    void cantidadNegativaEsRechazada() {

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
    void inventarioInsuficienteEsRechazado() {

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

        assertEquals(3, product.getExistencia());
    }

    @Test
    void actualizaExistenciaDespuesDeLaVenta() {

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

        Map<String, Integer> requestedProducts = new LinkedHashMap<>();
        requestedProducts.put("P001", 3);

        service.processSale(requestedProducts);

        assertEquals(5, product.getExistencia());

        assertSame(
                product,
                productRepository.products.get("P001"));
    }

    @Test
    void guardaLaVentaYCalculaElTotal() {

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
                new BigDecimal("4300.00").compareTo(sale.getTotal()));

        assertEquals(1, saleRepository.sales.size());

        assertSame(
                sale,
                saleRepository.sales.get(0));
    }

    private static class InMemoryProductRepositoryFake
            implements ProductRepositoryPort {

        private final Map<String, Product> products =
                new LinkedHashMap<>();

        @Override
        public Optional<Product> findByCodigo(String codigo) {
            return Optional.ofNullable(products.get(codigo));
        }

        @Override
        public void save(Product product) {
            products.put(product.getCodigo(), product);
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

        @Override
        public List<Sale> findAll() {
            return new ArrayList<>(sales);
        }
    }
}