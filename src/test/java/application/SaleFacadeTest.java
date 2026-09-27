package application;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.example.application.facade.SaleFacade;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.ports.inbound.ProcessSaleUseCase;

class SaleFacadeTest {

    @Test
    void procesarVentaDelegaCorrectamente() {
        Product product = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
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
            public Sale processSale(Map<String, Integer> productosSolicitados) {
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
}
