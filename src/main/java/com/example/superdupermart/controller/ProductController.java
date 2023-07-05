package com.example.superdupermart.controller;

import com.example.superdupermart.domain.Order;
import com.example.superdupermart.domain.Product;
import com.example.superdupermart.domain.User;
import com.example.superdupermart.dto.common.DataResponse;
import com.example.superdupermart.dto.common.MessageResponse;
import com.example.superdupermart.dto.common.ServiceStatus;
import com.example.superdupermart.dto.product.UpdateProductRequest;
import com.example.superdupermart.service.OrderService;
import com.example.superdupermart.service.ProductService;
import com.example.superdupermart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public ProductController(ProductService productService, UserService userService, OrderService orderService) {
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/products/all")
    public DataResponse getAllProducts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        List<Product> productList = productService.getAllProducts();
        if(user.getRole() == 0) {
            List<Product> productsUser = productList.stream()
                    .filter(product -> product.getQuantity() > 0)
                    .map(product ->
                            Product.builder()
                                    .id(product.getId())
                                    .description(product.getDescription())
                                    .name(product.getName())
                                    .retail_price(product.getRetail_price())
                                    .build())
                    .collect(Collectors.toList());
            return DataResponse.builder()
                    .success(true)
                    .message("Success get all products")
                    .data(productsUser)
                    .build();
        }
        List<Product> products = productService.getAllProducts();
        return DataResponse.builder()
                .success(true)
                .message("Success get all products")
                .data(products)
                .build();
    }

    @GetMapping("/products/{id}")
    public DataResponse getOneProduct(@PathVariable int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        Product product = productService.getProductById(id);
        if(user.getRole() == 0) {
            return DataResponse.builder()
                    .success(true)
                    .message("Successfully get all products")
                    .data(Product.builder()
                            .id(product.getId())
                            .description(product.getDescription())
                            .name(product.getName())
                            .retail_price(product.getRetail_price())
                            .build())
                    .build();
        }
        return DataResponse.builder()
                .success(true)
                .message("Successfully get all products")
                .data(product)
                .build();
    }

    @PatchMapping("/products/{id}")
    public MessageResponse updateProductInfo(@RequestBody UpdateProductRequest request,
                                             @PathVariable int id) {
        Product product = Product.builder().id(id)
                .description(request.getDescription())
                .name(request.getName())
                .quantity(request.getQuantity())
                .retail_price(request.getRetail_price())
                .wholesale_price(request.getWholesale_price()).build();
        productService.updateProduct(product);
        return MessageResponse.builder().serviceStatus(
                ServiceStatus.builder().success(true).build()
        ).message("Successfully update product info").build();
    }

    @PostMapping("/products")
    public MessageResponse createProduct(@RequestBody UpdateProductRequest request) {
        Product product = Product.builder()
                .description(request.getDescription())
                .name(request.getName())
                .quantity(request.getQuantity())
                .retail_price(request.getRetail_price())
                .wholesale_price(request.getWholesale_price()).build();
        productService.createProduct(product);
        return MessageResponse.builder().serviceStatus(
                ServiceStatus.builder().success(true).build()
        ).message("Successfully create product").build();
    }

}
