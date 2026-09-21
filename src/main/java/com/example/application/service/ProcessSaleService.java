package com.example.application.service;

import java.util.Map;

import com.example.domain.exception.ProductNotFound;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.ports.inbound.ProcessSaleUseCase;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;

public class ProcessSaleService implements ProcessSaleUseCase {

	private final ProductRepositoryPort productRepository;
	private final SaleRepositoryPort saleRepository;

	public ProcessSaleService(ProductRepositoryPort productRepository, SaleRepositoryPort saleRepository) {
		this.productRepository = productRepository;
		this.saleRepository = saleRepository;
	}

	@Override
	public void registrarProducto(Product product) {
		productRepository.save(product);
	}

	@Override
	public Sale crearVenta(Map<String, Integer> productos) {
		if (productos == null) {
			throw new IllegalArgumentException("Los productos de la venta no pueden ser nulos");
		}

		Sale sale = new Sale();
		for (Map.Entry<String, Integer> item : productos.entrySet()) {
			Product product = productRepository.findByCodigo(item.getKey())
					.orElseThrow(() -> new ProductNotFound(item.getKey()));
			sale.agregarDetalle(product, item.getValue());
		}
		return sale;
	}

	@Override
	public void confirmarVenta(Sale sale) {
		saleRepository.save(sale);
	}
}
