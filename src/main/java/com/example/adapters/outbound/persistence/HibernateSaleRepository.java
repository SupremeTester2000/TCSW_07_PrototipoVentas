package com.example.adapters.outbound.persistence;

import com.example.adapters.outbound.persistence.entity.ProductEntity;
import com.example.adapters.outbound.persistence.entity.SaleDetailEntity;
import com.example.adapters.outbound.persistence.entity.SaleEntity;
import com.example.domain.model.Sale;
import com.example.domain.model.SaleDetail;
import com.example.ports.outbound.SaleRepositoryPort;

public final class HibernateSaleRepository implements SaleRepositoryPort {

    private final HibernateSessionManager sessionManager;

    public HibernateSaleRepository(HibernateSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void save(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("La venta no puede ser nula.");
        }
        sessionManager.inTransaction(session -> {
            SaleEntity saleEntity = new SaleEntity(
                    sale.getSubtotal(),
                    sale.getDiscountAmount(),
                    sale.getTaxAmount(),
                    sale.getTotal());

            short lineNumber = 1;
            for (SaleDetail detail : sale.getDetails()) {
                ProductEntity product = session.getReference(
                        ProductEntity.class,
                        detail.getProduct().getCode());
                saleEntity.addDetail(new SaleDetailEntity(
                        lineNumber++,
                        product,
                        detail.getQuantity(),
                        detail.getCapturedPrice()));
            }
            session.persist(saleEntity);
            return null;
        });
    }
}