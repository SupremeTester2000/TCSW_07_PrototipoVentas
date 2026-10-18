package adapters.swing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.example.adapters.inbound.swing.controller.CartController;
import com.example.adapters.inbound.swing.model.ProductDTO;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.ports.inbound.SaleFacadePort;

class CartControllerTest {

    @Test
    void addingSameProductAccumulatesQuantityAndUpdatesTable() {
        CartController controller = new CartController(new FakeSaleFacade());
        ProductDTO product = product("P001", 8);

        controller.addProduct(product, 2);
        controller.addProduct(product, 3);

        assertEquals(1, controller.getTableModel().getRowCount());
        assertEquals(5, controller.getTableModel().getValueAt(0, 1));
        assertEquals(new BigDecimal("50.00"), controller.getTableModel().getValueAt(0, 3));
    }

    @Test
    void rejectsMissingProductInvalidQuantityAndInsufficientStock() {
        CartController controller = new CartController(new FakeSaleFacade());
        ProductDTO availableProduct = product("P001", 8);

        assertThrows(IllegalArgumentException.class, () -> controller.addProduct(null, 1));
        assertThrows(IllegalArgumentException.class, () -> controller.addProduct(availableProduct, 0));
        assertThrows(IllegalArgumentException.class, () -> controller.addProduct(availableProduct, 9));
    }

    @Test
    void requestCannotBeBuiltForAnEmptyCart() {
        CartController controller = new CartController(new FakeSaleFacade());

        assertThrows(IllegalArgumentException.class, controller::buildRequest);
        assertThrows(IllegalArgumentException.class, () -> controller.removeProduct(0));
    }

    @Test
    void removingItemEmptiesTable() {
        CartController controller = new CartController(new FakeSaleFacade());
        controller.addProduct(product("P001", 8), 1);

        controller.removeProduct(0);

        assertEquals(0, controller.getTableModel().getRowCount());
    }

    @Test
    void previewReturnsSaleTotalsAsPresentationDto() {
        CartController controller = new CartController(new FakeSaleFacade());
        controller.addProduct(product("P001", 8), 2);

        assertEquals(new BigDecimal("20.00"), controller.previewSale().getSubtotal());
        assertEquals(new BigDecimal("0.00"), controller.previewSale().getDiscount());
        assertEquals(new BigDecimal("3.20"), controller.previewSale().getTaxes());
        assertEquals(new BigDecimal("23.20"), controller.previewSale().getTotal());
    }

    private static ProductDTO product(String code, int stock) {
        return new ProductDTO(code, "Producto", new BigDecimal("10.00"), stock);
    }

    private static final class FakeSaleFacade implements SaleFacadePort {
        @Override
        public Sale processSale(Map<String, Integer> products) {
            return sale(products);
        }

        @Override
        public Sale createSale(Map<String, Integer> products) {
            return sale(products);
        }

        @Override
        public void registerProduct(Product product) {
            throw new UnsupportedOperationException("No se invoca en estas pruebas.");
        }

        @Override
        public void confirmSale(Sale sale) {
            throw new UnsupportedOperationException("No se invoca en estas pruebas.");
        }

        @Override
        public List<Product> listProducts() {
            return Collections.singletonList(new Product(
                    "P001", "Producto", new BigDecimal("10.00"), 8));
        }

        private Sale sale(Map<String, Integer> quantities) {
            Sale sale = new Sale();
            if (quantities.containsKey("P001")) {
                sale.addDetail(new Product("P001", "Producto", new BigDecimal("10.00"), 8),
                        quantities.get("P001"));
            }
            return sale;
        }
    }
}