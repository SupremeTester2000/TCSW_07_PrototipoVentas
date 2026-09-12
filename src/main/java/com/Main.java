package com;

import java.math.BigDecimal;
import java.util.logging.Logger;
import domain.Producto;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class.getName());
        // Crear un producto de ejemplo
        Producto producto = new Producto("P001", "Producto de ejemplo", new BigDecimal("10.99"), 100);

        // Mostrar información del producto
        logger.info("Código: " + producto.getCodigo());
        logger.info("Nombre: " + producto.getNombre());
        logger.info("Precio: " + producto.getPrecio());
        logger.info("Existencia: " + producto.getExistencia());
    }
}
