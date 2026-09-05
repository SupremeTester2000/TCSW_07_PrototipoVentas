package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Venta {

    private final List<DetalleVenta> detalles = new ArrayList<>();

    public void agregarDetalle(Producto producto, int cantidad) {
        DetalleVenta nuevoDetalle = new DetalleVenta(producto, cantidad);
        detalles.add(nuevoDetalle);
    }

    public List<DetalleVenta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public double getTotal() {
        if (detalles.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (DetalleVenta detalle : detalles) {
            total += detalle.getSubtotal();
        }
        return total;
    }
}