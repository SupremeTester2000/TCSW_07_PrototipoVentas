package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.domain.model.Product;

class ProductTest {

    @Test
    void testValidProductCreation() {
        Product product = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);

        assertEquals("P001", product.getCode());
        assertEquals("Laptop", product.getName());
        assertEquals(0, new BigDecimal("1200.00").compareTo(product.getPrice()));
    }

    @Test
    void testRejectNegativePrice() {
        BigDecimal negativePrice = new BigDecimal("-50.00");

        assertThrows(IllegalArgumentException.class, () -> new Product("P002", "Mouse", negativePrice, 5));
    }

    @Test
    void testRejectNegativeStock() {
        BigDecimal validPrice = new BigDecimal("100.00");

        assertThrows(IllegalArgumentException.class, () -> new Product("P003", "Teclado", validPrice, -3));
    }

    @Test
    void testAllowProductBoundaryValues() {
        Product product = new Product("P000", "Muestra Gratis", BigDecimal.ZERO, 0);

        assertEquals(0, BigDecimal.ZERO.compareTo(product.getPrice()));
        assertEquals(0, product.getStock());
    }
}
