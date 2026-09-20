package com.example.adapters.inbound;

import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.ports.inbound.ProcessSaleUseCase;

public class SaleConsoleController {

	private final ProcessSaleUseCase processSaleUseCase;

	public SaleConsoleController(ProcessSaleUseCase processSaleUseCase) {
		this.processSaleUseCase = processSaleUseCase;
	}

	public void registrarProducto(Product product) {
		processSaleUseCase.registrarProducto(product);
	}

	public Sale crearVenta(Map<String, Integer> productos) {
		return processSaleUseCase.crearVenta(productos);
	}

	public void confirmarVenta(Sale sale) {
		processSaleUseCase.confirmarVenta(sale);
	}
}
