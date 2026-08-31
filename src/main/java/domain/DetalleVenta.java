package domain;

public class DetalleVenta {

    private final Producto producto;
    private final int cantidad;
    private final double precioCapturado;

    public DetalleVenta(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
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
}
