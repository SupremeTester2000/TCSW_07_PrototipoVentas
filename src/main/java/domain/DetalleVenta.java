package domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Objeto de Valor que representa una línea dentro de la venta.
 * Es inmutable y se valida contra las existencias del producto.
 */

public final class DetalleVenta {

    private final Producto producto;
    private final int cantidad;
    private final BigDecimal precioCapturado;

    public DetalleVenta(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (cantidad > producto.getExistencia()) {
            throw new IllegalArgumentException("La cantidad solicitada supera las existencias disponibles.");
        }

        this.producto = producto;
        this.cantidad = cantidad;
        this.precioCapturado = producto.getPrecio();
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioCapturado() {
        return precioCapturado;
    }

    public BigDecimal getSubtotal() {
        return precioCapturado.multiply(BigDecimal.valueOf(cantidad));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleVenta match = (DetalleVenta) o;
        return cantidad == match.cantidad 
                && Objects.equals(precioCapturado, match.precioCapturado) 
                && Objects.equals(producto, match.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(producto, cantidad, precioCapturado);
    }
}