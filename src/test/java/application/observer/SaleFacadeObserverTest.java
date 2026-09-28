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
import com.example.application.observer.ActualizadorStock;
import com.example.application.service.ProcessSaleService;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.observer.EmisorVentaConfirmada;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;

class SaleFacadeObserverTest {

    @Test
    void procesaVentaYNotificaActualizadorStock() {

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

        EmisorVentaConfirmada emisor =
                new EmisorVentaConfirmada();

        ActualizadorStock actualizadorStock =
                new ActualizadorStock(productRepository);

        emisor.registrarObservador(actualizadorStock);

        SaleFacade facade =
                new SaleFacade(
                        processSaleService,
                        emisor);

        Map<String, Integer> productos =
                new LinkedHashMap<>();

        productos.put("P001", 2);

        Sale sale = facade.procesarVenta(productos);

        assertEquals(1, sale.getDetalles().size());
        assertEquals(
                0,
                new BigDecimal("2784.00").compareTo(
                        sale.getTotal()));

        assertEquals(8, product.getExistencia());
        assertEquals(1, saleRepository.sales.size());
    }

    private static class InMemoryProductRepositoryFake
            implements ProductRepositoryPort {

        private final Map<String, Product> products =
                new LinkedHashMap<>();

        @Override
        public void save(Product product) {
            products.put(product.getCodigo(), product);
        }

        @Override
        public Optional<Product> findByCodigo(String codigo) {
            return Optional.ofNullable(products.get(codigo));
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