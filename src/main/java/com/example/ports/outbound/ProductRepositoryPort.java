package com.example.ports.outbound;

import com.example.domain.model.Product;
import java.util.Optional;

public interface ProductRepositoryPort {

    Optional<Product> findByCodigo(String codigo);

    void save(Product product);
}