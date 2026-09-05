package domain;

import java.util.Objects;

/**
 * Objeto de Valor que representa una línea dentro de la venta.
 * Es inmutable y se valida contra las existencias del producto.
 */
public final class DetalleVenta {

    private final Producto producto;
    private final int cantidad;
    private final double precioCapturado;

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

    public double getPrecioCapturado() {
        return precioCapturado;
    }

    public double getSubtotal() {
        return cantidad * precioCapturado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleVenta match = (DetalleVenta) o;
        return cantidad == match.cantidad 
                && Double.compare(match.precioCapturado, precioCapturado) == 0 
                && Objects.equals(producto, match.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(producto, cantidad, precioCapturado);
    }
}