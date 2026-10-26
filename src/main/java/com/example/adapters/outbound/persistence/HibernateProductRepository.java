package com.example.adapters.outbound.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.adapters.outbound.persistence.entity.ProductEntity;
import com.example.domain.model.Product;
import com.example.ports.outbound.ProductRepositoryPort;

public final class HibernateProductRepository implements ProductRepositoryPort {

    private final HibernateSessionManager sessionManager;

    public HibernateProductRepository(HibernateSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        sessionManager.inTransaction(session -> {
            session.merge(ProductEntity.fromDomain(product));
            return null;
        });
    }

    @Override
    public Optional<Product> findByCode(String code) {
        return sessionManager.inTransaction(session -> session
                .createSelectionQuery(
                        "from ProductEntity product where product.code = :code",
                        ProductEntity.class)
                .setParameter("code", code)
                .uniqueResultOptional()
                .map(ProductEntity::toDomain));
    }

    @Override
    public List<Product> findAll() {
        return sessionManager.inTransaction(session -> session
                .createSelectionQuery("from ProductEntity product order by product.code",
                        ProductEntity.class)
                .getResultList()
                .stream()
                .map(ProductEntity::toDomain)
                .collect(Collectors.toList()));
    }
}