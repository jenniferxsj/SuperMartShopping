package com.example.superdupermart.controller;

import com.example.superdupermart.domain.User;
import com.example.superdupermart.dto.common.MessageResponse;
import com.example.superdupermart.dto.common.ServiceStatus;
import com.example.superdupermart.dto.order.CreateOrderRequest;
import com.example.superdupermart.dto.product.ProductRequest;
import com.example.superdupermart.service.OrderService;
import com.example.superdupermart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/orders")
    public MessageResponse placeOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        orderService.placeOrder(user, createOrderRequest);
        return MessageResponse.builder()
                .serviceStatus(
                        ServiceStatus.builder().success(true).build()
                ).message("Product added to order").build();
    }
}
