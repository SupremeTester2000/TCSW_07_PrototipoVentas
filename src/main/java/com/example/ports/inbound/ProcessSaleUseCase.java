package com.example.ports.inbound;

import com.example.domain.model.Sale;
import java.util.Map;

public interface ProcessSaleUseCase {

    Sale processSale(Map<String, Integer> productosSolicitados);
}