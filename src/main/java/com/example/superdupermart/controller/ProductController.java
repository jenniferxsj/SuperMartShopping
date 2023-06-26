package com.example.superdupermart.controller;

import com.example.superdupermart.domain.Product;
import com.example.superdupermart.dto.common.DataResponse;
import com.example.superdupermart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/all")
    public DataResponse getAllStockProducts() {
        List<Product> products = productService.getAllStockProducts().stream()
                .map(product ->
                    Product.builder().description(product.getDescription())
                            .name(product.getName())
                            .retail_price(product.getRetail_price())
                            .build())
                .collect(Collectors.toList());
        return DataResponse.builder()
                .success(true)
                .message("Success get all products")
                .data(products)
                .build();
    }

    @GetMapping("/products/{id}")
    public DataResponse getOneProduct(@PathVariable int id) {
        Product product = productService.getProductById(id);
        return DataResponse.builder()
                .success(true)
                .message("Successfully get all products")
                .data(Product.builder()
                        .description(product.getDescription())
                        .name(product.getName())
                        .retail_price(product.getRetail_price())
                        .build())
                .build();
    }
}
