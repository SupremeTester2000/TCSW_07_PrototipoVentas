package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class VentaTest {

    @Test
    void agregaDetallesYCalculaTotalExacto() {
        Producto producto1 = new Producto(1, "Laptop", 1200.0, "Portátil");
        Producto producto2 = new Producto(2, "Mouse", 250.0, "Accesorio");
        Venta venta = new Venta();

        venta.agregarDetalle(producto1, 2);
        venta.agregarDetalle(producto2, 3);

        assertEquals(2, venta.getDetalles().size());
        assertEquals(2400.0, venta.getDetalles().get(0).getSubtotal(), 0.0001);
        assertEquals(750.0, venta.getDetalles().get(1).getSubtotal(), 0.0001);
        assertEquals(3150.0, venta.getTotal(), 0.0001);
    }

    @Test
    void rechazaCantidadCeroOInvalida() {
        Producto producto = new Producto(10, "Teclado", 400.0, "Teclado mecánico");
        Venta venta = new Venta();

        assertThrows(IllegalArgumentException.class, () -> venta.agregarDetalle(producto, 0));
        assertThrows(IllegalArgumentException.class, () -> venta.agregarDetalle(producto, -2));
    }

    @Test
    void siLaInsercionFallaLaListaNoSeAltera() {
        Producto producto = new Producto(20, "Monitor", 3500.0, "Pantalla");
        Venta venta = new Venta();
        venta.agregarDetalle(producto, 1);

        assertThrows(IllegalArgumentException.class, () -> venta.agregarDetalle(producto, 0));

        assertEquals(1, venta.getDetalles().size());
        assertEquals(3500.0, venta.getTotal(), 0.0001);
    }

    @Test
    void precioHistoricoNoCambiaAlActualizarCatalogo() {
        Producto producto = new Producto(30, "Impresora", 600.0, "Impresora láser");
        Venta venta = new Venta();

        venta.agregarDetalle(producto, 2);
        DetalleVenta detalle = venta.getDetalles().get(0);

        producto.actualizarPrecio(900.0);

        assertEquals(1200.0, detalle.getSubtotal(), 0.0001);
        assertEquals(1200.0, venta.getTotal(), 0.0001);
    }
}
