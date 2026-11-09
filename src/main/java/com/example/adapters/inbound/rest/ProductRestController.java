package com.example.adapters.inbound.rest;

import java.util.List;
import java.util.Map;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.ports.inbound.SaleFacadePort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductRestController {

    private final SaleFacadePort saleFacadePort;

    public ProductRestController(SaleFacadePort saleFacadePort) {
        this.saleFacadePort = saleFacadePort;
    }

    @GetMapping("/products")
    public List<Product> listProducts() {
        return saleFacadePort.listProducts();
    }

    @PostMapping("/sales")
    public Sale processSale(@RequestBody Map<String, Integer> products) {
        return saleFacadePort.processSale(products);
    }
}
