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
        for (SaleDetail detail : validatedSale.getDetails()) {
            configuredSale.addDetail(detail);
        }
        return configuredSale;
    }

    SaleDetail createSaleDetail(Product product, int quantity);

    default Sale buildSale(List<SaleDetail> details) {
        return createSale(details);
    }

    default SaleDetail createDetail(Product product, int quantity) {
        return createSaleDetail(product, quantity);
    }
}
