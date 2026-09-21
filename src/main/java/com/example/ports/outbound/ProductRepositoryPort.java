package com.example.ports.outbound;

<<<<<<< HEAD
import com.example.domain.model.Product;
import java.util.Optional;

public interface ProductRepositoryPort {

    Optional<Product> findByCodigo(String codigo);

    void save(Product product);
}
=======
import java.util.List;
import java.util.Optional;

import com.example.domain.model.Product;

public interface ProductRepositoryPort {

	void save(Product product);

	Optional<Product> findByCodigo(String codigo);

	List<Product> findAll();
}
>>>>>>> origin/main
