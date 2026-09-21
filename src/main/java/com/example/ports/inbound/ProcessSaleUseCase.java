package com.example.ports.inbound;

<<<<<<< HEAD
import com.example.domain.model.Sale;
import java.util.Map;

public interface ProcessSaleUseCase {

    Sale processSale(Map<String, Integer> productosSolicitados);
}
=======
import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;

public interface ProcessSaleUseCase {

	void registrarProducto(Product product);

	Sale crearVenta(Map<String, Integer> productos);

	void confirmarVenta(Sale sale);
}
>>>>>>> origin/main
