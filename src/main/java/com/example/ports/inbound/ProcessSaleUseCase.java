package com.example.ports.inbound;

import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;

public interface ProcessSaleUseCase {

    void registrarProducto(Product product);

    Sale crearVenta(Map<String, Integer> productos);

    void confirmarVenta(Sale sale);

    Sale processSale(Map<String, Integer> productosSolicitados);
}
