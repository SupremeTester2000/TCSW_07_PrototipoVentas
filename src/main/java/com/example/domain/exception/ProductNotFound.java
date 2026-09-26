package com.example.domain.exception;

public class ProductNotFound extends RuntimeException {

    public ProductNotFound(String codigo) {
        super("Producto no encontrado: " + codigo);
    }
}
