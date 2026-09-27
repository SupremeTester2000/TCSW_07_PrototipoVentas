package com.example.domain.strategy;

import java.math.BigDecimal;

import com.example.domain.model.Sale;

@FunctionalInterface
public interface TaxStrategy {

    BigDecimal calculateTax(Sale sale, BigDecimal taxableAmount);
}