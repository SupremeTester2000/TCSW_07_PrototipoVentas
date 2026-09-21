package com.example.domain.exception;

public class ProductNotFound extends RuntimeException {

	public ProductNotFound(String codigo) {
		super("No se encontró el producto con código: " + codigo);
	}
}
