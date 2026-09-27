package com.example.domain.strategy;

import java.math.BigDecimal;
import java.util.function.Function;

import com.example.domain.model.Sale;

@FunctionalInterface
public interface DiscountStrategy extends Function<Sale, BigDecimal> {

    BigDecimal calculateDiscount(Sale sale);

    @Override
    default BigDecimal apply(Sale sale) {
        return calculateDiscount(sale);
    }
}