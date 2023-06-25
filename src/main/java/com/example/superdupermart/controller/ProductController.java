package com.example.superdupermart.controller;

import com.example.superdupermart.dto.common.DataResponse;
import com.example.superdupermart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/all")
    public DataResponse getAllStockProducts() {
        return DataResponse.builder()
                .success(true)
                .message("Success get all products")
                .data(productService.getAllStockProducts())
                .build();
    }

    @GetMapping("/products/{id}")
    public DataResponse getOneProduct(@PathVariable int id) {
        return DataResponse.builder()
                .success(true)
                .message("Success get all products")
                .data(productService.getProductById(id))
                .build();
    }
}
