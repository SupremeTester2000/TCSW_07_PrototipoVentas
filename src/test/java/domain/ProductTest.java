package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.domain.model.Product;

class ProductTest {

    @Test
    public void testProductoCreacionValida() {
        Product product = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);

        assertEquals("P001", product.getCodigo());
        assertEquals("Laptop", product.getNombre());
        assertEquals(0, new BigDecimal("1200.00").compareTo(product.getPrecio()));
    }

    @Test
    public void testRechazoPrecioNegativo() {
        BigDecimal precioNegativo = new BigDecimal("-50.00");

        assertThrows(IllegalArgumentException.class, () -> new Product("P002", "Mouse", precioNegativo, 5));
    }

    @Test
    public void testRechazoExistenciaNegativa() {
        BigDecimal precioValido = new BigDecimal("100.00");

        assertThrows(IllegalArgumentException.class, () -> new Product("P003", "Teclado", precioValido, -3));
    }

    @Test
    public void testProductoValoresLimitePermitidos() {
        Product product = new Product("P000", "Muestra Gratis", BigDecimal.ZERO, 0);

        assertEquals(0, BigDecimal.ZERO.compareTo(product.getPrecio()));
        assertEquals(0, product.getExistencia());
    }
}
