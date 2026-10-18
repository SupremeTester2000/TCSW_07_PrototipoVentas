package com.example.adapters.inbound.swing.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SaleRequestDTO {

    private final Map<String, Integer> quantitiesByProductCode;

    public SaleRequestDTO(Map<String, Integer> quantitiesByProductCode) {
        if (quantitiesByProductCode == null || quantitiesByProductCode.isEmpty()) {
            throw new IllegalArgumentException("El carrito debe contener al menos un producto.");
        }
        Map<String, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : quantitiesByProductCode.entrySet()) {
            if (entry.getKey() == null || entry.getKey().trim().isEmpty()) {
                throw new IllegalArgumentException("El código del producto es obligatorio.");
            }
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
            copy.put(entry.getKey(), entry.getValue());
        }
        this.quantitiesByProductCode = Collections.unmodifiableMap(copy);
    }

    public Map<String, Integer> getQuantitiesByProductCode() {
        return quantitiesByProductCode;
    }
}