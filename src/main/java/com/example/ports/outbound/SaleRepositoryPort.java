package com.example.ports.outbound;

import java.util.List;

import com.example.domain.model.Sale;

public interface SaleRepositoryPort {

    void save(Sale sale);

    List<Sale> findAll();
}
