package com.example.domain.exception;

public class ProductNotFound extends RuntimeException {

    public ProductNotFound(String code) {
        super("Producto no encontrado: " + code);
    }
}
