package com.example.adapters.outbound;

import java.util.ArrayList;
import java.util.List;

import com.example.domain.model.Sale;
import com.example.ports.outbound.SaleRepositoryPort;

public class InMemorySaleRepository implements SaleRepositoryPort {

	private final List<Sale> sales = new ArrayList<>();

	@Override
	public void save(Sale sale) {
		if (sale == null) {
			throw new IllegalArgumentException("La venta no puede ser nula");
		}
		sales.add(sale);
	}

	@Override
	public List<Sale> findAll() {
		return new ArrayList<>(sales);
	}
}
