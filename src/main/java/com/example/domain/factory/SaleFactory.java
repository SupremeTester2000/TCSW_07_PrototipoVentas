package com.example.domain.factory;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import com.example.domain.model.Sale;
import com.example.domain.model.Product;
import com.example.domain.model.SaleDetail;
import com.example.domain.strategy.TaxStrategy;

public interface SaleFactory {

    Sale createSale(List<SaleDetail> details);

    default Sale createSale(
            List<SaleDetail> details,
            Function<Sale, BigDecimal> discountPolicy,
            TaxStrategy taxStrategy) {
        Sale validatedSale = createSale(details);
        Sale configuredSale = new Sale(discountPolicy, taxStrategy);
        for (SaleDetail detail : validatedSale.getDetalles()) {
            configuredSale.agregarDetalle(detail);
        }
        return configuredSale;
    }

    SaleDetail createSaleDetail(Product producto, int cantidad);

    default Sale crearVenta(List<SaleDetail> detalles) {
        return createSale(detalles);
    }

    default SaleDetail crearDetalle(Product producto, int cantidad) {
        return createSaleDetail(producto, cantidad);
    }
}
