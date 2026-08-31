package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Venta {

    private final List<DetalleVenta> detalles = new ArrayList<>();

    public void agregarDetalle(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        DetalleVenta nuevoDetalle = new DetalleVenta(producto, cantidad);
        detalles.add(nuevoDetalle);
    }

    public List<DetalleVenta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public double getTotal() {
        double total = 0.0;
        for (DetalleVenta detalle : detalles) {
            total += detalle.getSubtotal();
        }
        return total;
    }
}
