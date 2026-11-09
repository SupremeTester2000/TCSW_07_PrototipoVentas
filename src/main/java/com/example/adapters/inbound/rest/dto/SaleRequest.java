package com.example.adapters.inbound.rest.dto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = SaleRequestDeserializer.class)
public final class SaleRequest {

    @Valid
    @NotEmpty
    private final Map<
            @NotBlank @Pattern(regexp = "P\\d{5}") String,
            @NotNull @Positive Integer> products;

    public SaleRequest(Map<String, Integer> products) {
        this.products = Collections.unmodifiableMap(new LinkedHashMap<>(products));
    }

    public Map<String, Integer> getProducts() {
        return products;
    }

    public Map<String, Integer> toProducts() {
        return new LinkedHashMap<>(products);
    }
}
