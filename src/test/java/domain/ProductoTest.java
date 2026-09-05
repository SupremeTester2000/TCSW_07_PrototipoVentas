package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void testProductoCreacionValida() {
        Producto producto = new Producto("P001", "Laptop", 1200.0, 10);
        assertEquals("P001", producto.getCodigo());
        assertEquals("Laptop", producto.getNombre());
        assertEquals(1200.0, producto.getPrecio(), 0.0001);
    }

    @Test
    void testRechazoPrecioNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Producto("P002", "Mouse", -50.0, 5);
        });
    }

    @Test
    void testRechazoExistenciaNegativa() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Producto("P003", "Teclado", 100.0, -3);
        });
    }

    @Test
    void testProductoValoresLimitePermitidos() {
        Producto producto = new Producto("P000", "Muestra Gratis", 0.0, 0);
        assertEquals(0.0, producto.getPrecio(), 0.0001);
        assertEquals(0, producto.getExistencia());
    }
}
