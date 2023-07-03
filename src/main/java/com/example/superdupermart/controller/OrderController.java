package com.example.superdupermart.controller;

import com.example.superdupermart.domain.Order;
import com.example.superdupermart.domain.User;
import com.example.superdupermart.dto.common.DataResponse;
import com.example.superdupermart.dto.common.MessageResponse;
import com.example.superdupermart.dto.common.ServiceStatus;
import com.example.superdupermart.dto.order.AllOrderResponse;
import com.example.superdupermart.dto.order.CreateOrderRequest;
import com.example.superdupermart.dto.product.ProductRequest;
import com.example.superdupermart.service.OrderService;
import com.example.superdupermart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
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
        System.out.println(createOrderRequest);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        orderService.placeOrder(user, createOrderRequest);
        return MessageResponse.builder()
                .serviceStatus(
                        ServiceStatus.builder().success(true).build()
                ).message("Product added to order").build();
    }

    @GetMapping("/orders/all")
    public AllOrderResponse getUserAllOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        List<Order> orderList = orderService.getUserAllOrders(user);
        return AllOrderResponse.builder()
                .serviceStatus(ServiceStatus.builder()
                        .success(true)
                        .message("successfully get all orders")
                        .build())
                .orders(orderList).build();
    }

    @GetMapping("/orders/{id}")
    public DataResponse getOneOrder(@PathVariable int id) {
        Order order = orderService.getOrderById(id);
        return DataResponse.builder()
                .success(true)
                .message("Successfully get requested order info")
                .data(order)
                .build();
    }
}
