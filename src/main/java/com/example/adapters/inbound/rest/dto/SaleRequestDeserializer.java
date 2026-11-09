package com.example.adapters.inbound.rest.dto;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public final class SaleRequestDeserializer extends JsonDeserializer<SaleRequest> {

    @Override
    public SaleRequest deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        Map<String, Integer> products = parser.readValueAs(
                new TypeReference<LinkedHashMap<String, Integer>>() { });
        return new SaleRequest(products);
    }
}
