package com.example.ports.outbound;

import com.example.domain.model.Sale;

public interface SaleRepositoryPort {

    void save(Sale sale);
}