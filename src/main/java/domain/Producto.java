package domain;

public class Producto {

    private int id;
    private String nombre;
    private double precio;
    private String descripcion;
    private int existencia;

    public Producto(int id, String nombre, double precio, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        if (existencia < 0) {
            throw new IllegalArgumentException("La existencia no puede ser negativa");
        }

        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getExistencia() {
        return existencia;
    }

    public void actualizarPrecio(double precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        this.precio = precio;
    }

    public void actualizarExistencia(int existencia) {
        if (existencia < 0) {
            throw new IllegalArgumentException("La existencia no puede ser negativa");
        }
        this.existencia = existencia;
    }
}
