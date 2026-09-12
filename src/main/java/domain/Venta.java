package domain;

import java.math.BigDecimal;
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

    public BigDecimal getTotal() {
        if (detalles.isEmpty()) {   
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            total = total.add(detalle.getSubtotal());
        }
        return total;
    }
}