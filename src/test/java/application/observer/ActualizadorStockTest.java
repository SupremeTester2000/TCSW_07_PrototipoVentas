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
import com.example.application.observer.ActualizadorStock;
import com.example.domain.observer.VentaConfirmada;
import com.example.ports.outbound.ProductRepositoryPort;

class ActualizadorStockTest {

    @Test
    void actualizaExistenciaAlRecibirVentaConfirmada() {
        Product product = new Product(
                "P001",
                "Laptop",
                new BigDecimal("1200.00"),
                10);

        InMemoryProductRepositoryFake repository =
                new InMemoryProductRepositoryFake();

        repository.save(product);

        Sale sale = new Sale();
        sale.agregarDetalle(product, 3);

        ActualizadorStock actualizador =
                new ActualizadorStock(repository);

        actualizador.actualizar(new VentaConfirmada(sale));

        assertEquals(7, product.getExistencia());
        assertEquals(7, repository.products.get("P001").getExistencia());
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
}