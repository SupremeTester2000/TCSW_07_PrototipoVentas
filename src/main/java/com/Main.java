package com;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.example.adapters.inbound.SaleConsoleController;
import com.example.adapters.outbound.InMemoryProductRepository;
import com.example.adapters.outbound.InMemorySaleRepository;
import com.example.application.service.ProcessSaleService;
import com.example.domain.model.Product;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class.getName());

        ProductRepositoryPort productRepository = new InMemoryProductRepository();
        SaleRepositoryPort saleRepository = new InMemorySaleRepository();
        SaleConsoleController controller = new SaleConsoleController(
            new ProcessSaleService(productRepository, saleRepository));

        Product product = new Product("P001", "Producto de ejemplo", new BigDecimal("10.99"), 100);
        controller.registrarProducto(product);

        logger.info("Código: " + product.getCodigo());
        logger.info("Nombre: " + product.getNombre());
        logger.info("Precio: " + product.getPrecio());
        logger.info("Existencia: " + product.getExistencia());
    }
}
