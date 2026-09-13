package domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    public void testProductoCreacionValida() {
        Producto producto = new Producto("P001", "Laptop", new BigDecimal("1200.00"), 10);
        
        assertEquals("P001", producto.getCodigo());
        assertEquals("Laptop", producto.getNombre());
        assertEquals(0, new BigDecimal("1200.00").compareTo(producto.getPrecio()));
    }

    @Test
    public void testRechazoPrecioNegativo() {
        BigDecimal precioNegativo = new BigDecimal("-50.00");
        
        assertThrows(IllegalArgumentException.class, () -> new Producto("P002", "Mouse", precioNegativo, 5));
    }

    @Test
    public void testRechazoExistenciaNegativa() {
        BigDecimal precioValido = new BigDecimal("100.00");
        
        assertThrows(IllegalArgumentException.class, () -> new Producto("P003", "Teclado", precioValido, -3));
    }

    @Test
    public void testProductoValoresLimitePermitidos() {
        Producto producto = new Producto("P000", "Muestra Gratis", BigDecimal.ZERO, 0);
        
        assertEquals(0, BigDecimal.ZERO.compareTo(producto.getPrecio()));
        assertEquals(0, producto.getExistencia());
    }
}