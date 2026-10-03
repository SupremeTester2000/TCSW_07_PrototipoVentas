package com.example.adapters.outbound;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.domain.model.Product;
import com.example.ports.outbound.ProductRepositoryPort;

public class InMemoryProductRepository implements ProductRepositoryPort {

	private final Map<String, Product> products = new LinkedHashMap<>();

	@Override
	public void save(Product product) {
		if (product == null) {
			throw new IllegalArgumentException("El producto no puede ser nulo");
		}
		products.put(product.getCode(), product);
	}

	@Override
	public Optional<Product> findByCode(String code) {
		return Optional.ofNullable(products.get(code));
	}

	@Override
	public List<Product> findAll() {
		return new ArrayList<>(products.values());
	}
}
