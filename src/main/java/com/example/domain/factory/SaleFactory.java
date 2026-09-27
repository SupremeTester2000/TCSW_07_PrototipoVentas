package com.example.domain.factory;

import java.util.List;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.model.SaleDetail;

public interface SaleFactory {

    Sale createSale(List<SaleDetail> details);

    SaleDetail createSaleDetail(Product producto, int cantidad);

    default Sale crearVenta(List<SaleDetail> detalles) {
        return createSale(detalles);
    }

    default SaleDetail crearDetalle(Product producto, int cantidad) {
        return createSaleDetail(producto, cantidad);
    }
}
