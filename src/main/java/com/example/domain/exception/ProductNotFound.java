package com.example.domain.exception;

public class ProductNotFound extends RuntimeException {

<<<<<<< HEAD
    public ProductNotFound(String codigo) {
        super("Producto no encontrado: " + codigo);
    }
}
=======
	public ProductNotFound(String codigo) {
		super("No se encontró el producto con código: " + codigo);
	}
}
>>>>>>> origin/main
