package com.example.adapters.inbound.rest;

import java.util.List;

import javax.validation.Valid;

import com.example.adapters.inbound.rest.dto.ProductRequest;
import com.example.adapters.inbound.rest.dto.ProductResponse;
import com.example.adapters.inbound.rest.dto.SaleRequest;
import com.example.adapters.inbound.rest.dto.SaleResponse;
import com.example.adapters.inbound.rest.mapper.RestDtoMapper;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.ports.inbound.SaleFacadePort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductRestController {

    private final SaleFacadePort saleFacadePort;
    private final RestDtoMapper mapper;

    public ProductRestController(SaleFacadePort saleFacadePort) {
        this.saleFacadePort = saleFacadePort;
        this.mapper = new RestDtoMapper();
    }

    @GetMapping("/products")
    public List<ProductResponse> listProducts() {
        return mapper.toProductResponses(saleFacadePort.listProducts());
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> registerProduct(
            @Valid @RequestBody ProductRequest request) {
        String code = saleFacadePort.getNextProductCode();
        Product product = mapper.toProduct(request, code);
        saleFacadePort.registerProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toProductResponse(product));
    }

    @PutMapping("/products/{code:P\\d{5}}")
    public ProductResponse updateProduct(
            @PathVariable String code,
            @Valid @RequestBody ProductRequest request) {
        Product product = mapper.toProduct(request, code);
        saleFacadePort.updateProduct(product);
        return mapper.toProductResponse(product);
    }

    @PostMapping("/sales")
    public SaleResponse processSale(@Valid @RequestBody SaleRequest request) {
        Sale sale = saleFacadePort.processSale(request.toProducts());
        return mapper.toSaleResponse(sale);
    }
}
