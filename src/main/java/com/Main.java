package com;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.example.domain.model.Product;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class.getName());

        Product product = new Product("P001", "Producto de ejemplo", new BigDecimal("10.99"), 100);

        logger.info("Código: " + product.getCodigo());
        logger.info("Nombre: " + product.getNombre());
        logger.info("Precio: " + product.getPrecio());
        logger.info("Existencia: " + product.getExistencia());
    }
}
