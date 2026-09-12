package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class VentaTest {

    @Test
    void agregaDetallesYCalculaTotalExacto() {
        Producto producto1 = new Producto("P001", "Laptop", new BigDecimal("1200.00"), 10);
        Producto producto2 = new Producto("P002", "Mouse", new BigDecimal("250.00"), 5);
        Venta venta = new Venta();

        venta.agregarDetalle(producto1, 2);
        venta.agregarDetalle(producto2, 3);

        assertEquals(2, venta.getDetalles().size());
        assertEquals(0, new BigDecimal("2400.00").compareTo(venta.getDetalles().get(0).getSubtotal()));
        assertEquals(0, new BigDecimal("750.00").compareTo(venta.getDetalles().get(1).getSubtotal()));
        assertEquals(0, new BigDecimal("3150.00").compareTo(venta.getTotal()));
    }

    @Test
    void rechazaCantidadCeroOInvalida() {
        Producto producto = new Producto("P003", "Teclado", new BigDecimal("400.00"), 5);
        Venta venta = new Venta();

        assertThrows(IllegalArgumentException.class, () -> venta.agregarDetalle(producto, 0));
        assertThrows(IllegalArgumentException.class, () -> venta.agregarDetalle(producto, -2));
    }

    @Test
    void rechazaCantidadQueExcedeExistencias() {
        Producto producto = new Producto("P006", "Tablet", new BigDecimal("3000.00"), 3);
        Venta venta = new Venta();

        assertThrows(IllegalArgumentException.class, () -> venta.agregarDetalle(producto, 10));
    }

    @Test
    void siLaInsercionFallaLaListaNoSeAltera() {
        Producto producto = new Producto("P004", "Monitor", new BigDecimal("3500.00"), 5);
        Venta venta = new Venta();
        venta.agregarDetalle(producto, 1);

        assertThrows(IllegalArgumentException.class, () -> venta.agregarDetalle(producto, 0));

        assertEquals(1, venta.getDetalles().size());
        assertEquals(0, new BigDecimal("3500.00").compareTo(venta.getTotal()));
    }

    @Test
    void precioHistoricoNoCambiaAlActualizarCatalogo() {
        Producto producto = new Producto("P005", "Impresora", new BigDecimal("600.00"), 5);
        Venta venta = new Venta();

        venta.agregarDetalle(producto, 2);
        DetalleVenta detalle = venta.getDetalles().get(0);

        producto.setPrecio(new BigDecimal("900.00"));

        assertEquals(0, new BigDecimal("1200.00").compareTo(detalle.getSubtotal()));
        assertEquals(0, new BigDecimal("1200.00").compareTo(venta.getTotal()));
    }
}