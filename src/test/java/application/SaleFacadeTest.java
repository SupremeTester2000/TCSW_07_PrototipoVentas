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
import com.example.domain.observer.EmisorVentaConfirmada;
import com.example.ports.inbound.ProcessSaleUseCase;

class SaleFacadeTest {

    @Test
    void procesarVentaDelegaCorrectamente() {
        Product product = new Product(
                "P001",
                "Laptop",
                new BigDecimal("1200.00"),
                10);

        Sale expectedSale = new Sale();
        expectedSale.agregarDetalle(product, 1);

        final Map<String, Integer> captured = new LinkedHashMap<>();

        ProcessSaleUseCase useCase = new ProcessSaleUseCase() {
            @Override
            public void registrarProducto(Product product) {
            }

            @Override
            public Sale crearVenta(Map<String, Integer> productos) {
                return expectedSale;
            }

            @Override
            public void confirmarVenta(Sale sale) {
            }

            @Override
            public Sale processSale(
                    Map<String, Integer> productosSolicitados) {
                captured.putAll(productosSolicitados);
                return expectedSale;
            }
        };

        SaleFacade facade = new SaleFacade(useCase);

        Map<String, Integer> request = new LinkedHashMap<>();
        request.put("P001", 1);

        Sale result = facade.procesarVenta(request);

        assertSame(expectedSale, result);
        assertSame(1, captured.get("P001"));
    }

    @Test
    void processSaleDelegaEnProcesarVenta() {
        Sale expectedSale = new Sale();

        ProcessSaleUseCase useCase = new ProcessSaleUseCase() {
            @Override
            public void registrarProducto(Product product) {
            }

            @Override
            public Sale crearVenta(Map<String, Integer> productos) {
                return expectedSale;
            }

            @Override
            public void confirmarVenta(Sale sale) {
            }

            @Override
            public Sale processSale(
                    Map<String, Integer> productosSolicitados) {
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
    void registrarProductoDelegaCorrectamente() {
        Product product = new Product(
                "P001",
                "Laptop",
                new BigDecimal("1200.00"),
                10);

        final Product[] capturedProduct = new Product[1];

        ProcessSaleUseCase useCase = new ProcessSaleUseCase() {
            @Override
            public void registrarProducto(Product product) {
                capturedProduct[0] = product;
            }

            @Override
            public Sale crearVenta(Map<String, Integer> productos) {
                return new Sale();
            }

            @Override
            public void confirmarVenta(Sale sale) {
            }

            @Override
            public Sale processSale(
                    Map<String, Integer> productosSolicitados) {
                return new Sale();
            }
        };

        SaleFacade facade = new SaleFacade(useCase);

        facade.registrarProducto(product);

        assertSame(product, capturedProduct[0]);
    }

    @Test
    void confirmarVentaDelegaCorrectamente() {
        Sale sale = new Sale();

        final Sale[] capturedSale = new Sale[1];

        ProcessSaleUseCase useCase = new ProcessSaleUseCase() {
            @Override
            public void registrarProducto(Product product) {
            }

            @Override
            public Sale crearVenta(Map<String, Integer> productos) {
                return new Sale();
            }

            @Override
            public void confirmarVenta(Sale sale) {
                capturedSale[0] = sale;
            }

            @Override
            public Sale processSale(
                    Map<String, Integer> productosSolicitados) {
                return new Sale();
            }
        };

        SaleFacade facade = new SaleFacade(useCase);

        facade.confirmarVenta(sale);

        assertSame(sale, capturedSale[0]);
    }

    @Test
    void rechazaCasoDeUsoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SaleFacade(null));
    }

    @Test
    void rechazaEmisorNulo() {
        ProcessSaleUseCase useCase = new ProcessSaleUseCase() {
            @Override
            public void registrarProducto(Product product) {
            }

            @Override
            public Sale crearVenta(Map<String, Integer> productos) {
                return new Sale();
            }

            @Override
            public void confirmarVenta(Sale sale) {
            }

            @Override
            public Sale processSale(
                    Map<String, Integer> productosSolicitados) {
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