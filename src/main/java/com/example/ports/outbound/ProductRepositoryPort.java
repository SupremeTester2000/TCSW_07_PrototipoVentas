package com.example.ports.outbound;

import java.util.List;
import java.util.Optional;

import com.example.domain.model.Product;

public interface ProductRepositoryPort {

	void save(Product product);

	Optional<Product> findByCodigo(String codigo);

	List<Product> findAll();
}
