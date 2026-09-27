package com;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.example.adapters.inbound.SaleConsoleController;
import com.example.adapters.outbound.InMemoryProductRepository;
import com.example.adapters.outbound.InMemorySaleRepository;
import com.example.application.facade.SaleFacade;
import com.example.application.service.ProcessSaleService;
import com.example.domain.factory.DefaultSaleFactory;
import com.example.domain.factory.SaleFactory;
import com.example.domain.strategy.FunctionalDiscountPolicies;
import com.example.domain.model.Product;
import com.example.domain.strategy.VatTaxStrategy;
import com.example.ports.inbound.ProcessSaleUseCase;
import com.example.ports.outbound.ProductRepositoryPort;
import com.example.ports.outbound.SaleRepositoryPort;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class.getName());

        ProductRepositoryPort productRepository = new InMemoryProductRepository();
        SaleRepositoryPort saleRepository = new InMemorySaleRepository();
        SaleFactory saleFactory = new DefaultSaleFactory();

        ProcessSaleUseCase processSaleUseCase = new ProcessSaleService(
                productRepository,
                saleRepository,
                saleFactory,
                FunctionalDiscountPolicies.percentage(new BigDecimal("5")),
                new VatTaxStrategy());

        SaleFacade saleFacade = new SaleFacade(processSaleUseCase);
        SaleConsoleController controller = new SaleConsoleController(saleFacade);

        Product product = new Product("P001", "Producto de ejemplo", new BigDecimal("10.99"), 100);
        controller.registrarProducto(product);

        logger.info("Código: " + product.getCodigo());
        logger.info("Nombre: " + product.getNombre());
        logger.info("Precio: " + product.getPrecio());
        logger.info("Existencia: " + product.getExistencia());
    }
}
