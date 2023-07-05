package com.example.superdupermart.controller;

import com.example.superdupermart.domain.Order;
import com.example.superdupermart.domain.OrderItem;
import com.example.superdupermart.domain.User;
import com.example.superdupermart.dto.common.DataResponse;
import com.example.superdupermart.dto.common.MessageResponse;
import com.example.superdupermart.dto.common.ServiceStatus;
import com.example.superdupermart.dto.order.AllOrderResponse;
import com.example.superdupermart.dto.order.CreateOrderRequest;
import com.example.superdupermart.dto.order.OrderDTO;
import com.example.superdupermart.dto.orderItem.OrderItemDTO;
import com.example.superdupermart.dto.product.ProductDTO;
import com.example.superdupermart.dto.product.ProductRequest;
import com.example.superdupermart.exception.NoAuthException;
import com.example.superdupermart.service.OrderService;
import com.example.superdupermart.service.UserService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        orderService.placeOrder(user, createOrderRequest);
        return MessageResponse.builder()
                .serviceStatus(
                        ServiceStatus.builder().success(true).build()
                ).message("Successfully placed order").build();
    }

    @GetMapping("/orders/all")
    public AllOrderResponse getUserAllOrders(@RequestParam(defaultValue = "0") Integer pageNumber,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        boolean isAdmin = user.getRole() == 1;

        List<Order> orderList = isAdmin ? orderService.getAllOrder() :  orderService.getUserAllOrders(user);
        Collections.sort(orderList, (o1, o2) -> o2.getDate_placed().compareTo(o1.getDate_placed()));
        List<OrderDTO> orderDTOS = orderList.stream().map(
                order -> OrderDTO.builder().id(order.getId())
                            .date_placed(order.getDate_placed())
                            .username(order.getUser().getUsername())
                        .order_status(order.getOrder_status())
                            .orderItemDTOList(order.getOrderItemList().stream().map(
                                    orderItem -> OrderItemDTO.builder().id(orderItem.getId())
                                            .purchased_price(orderItem.getPurchased_price())
                                            .wholesale_price(isAdmin ? orderItem.getWholesale_price() : 0)
                                            .quantity(orderItem.getQuantity())
                                            .productDTO(ProductDTO.builder()
                                                    .id(orderItem.getProduct().getId())
                                                    .name(orderItem.getProduct().getName())
                                                    .description(orderItem.getProduct().getDescription())
                                                    .build()).build()
                            ).collect(Collectors.toList())).build()
        ).collect(Collectors.toList());
        return AllOrderResponse.builder()
                .serviceStatus(ServiceStatus.builder()
                        .success(true)
                        .message("successfully get all orders")
                        .build())
                .orders(orderDTOS).build();
    }

    @GetMapping("/orders/{id}")
    public DataResponse getOneOrder(@PathVariable int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        boolean isAdmin = user.getRole() == 1;
        Order order = orderService.getOrderById(id);
        List<OrderItemDTO> orderItemList = order.getOrderItemList().stream()
                .map(item -> OrderItemDTO.builder().id(item.getId())
                        .quantity(item.getQuantity())
                        .purchased_price(item.getPurchased_price())
                        .wholesale_price(isAdmin ? item.getWholesale_price() : 0)
                        .productDTO(ProductDTO.builder()
                                .id(item.getProduct().getId())
                                .name(item.getProduct().getName())
                                .description(item.getProduct().getDescription())
                                .build())
                        .build())
                .collect(Collectors.toList());

        OrderDTO orderDTO = OrderDTO.builder().id(order.getId())
                .username(order.getUser().getUsername())
                .date_placed(order.getDate_placed())
                .order_status(order.getOrder_status())
                .orderItemDTOList(orderItemList).build();

        return DataResponse.builder()
                .success(true)
                .message("Successfully get requested order info")
                .data(orderDTO)
                .build();
    }

    @PatchMapping("/orders/{id}/cancel")
    public MessageResponse cancelOrder(@PathVariable int id) {
        int result = orderService.cancelOrder(id);
        if(result == 0) {
            return MessageResponse.builder()
                    .serviceStatus(
                            ServiceStatus.builder().success(true).build()
                    ).message("Sorry, the order has completed and cannot be canceled").build();
        } else if(result == 2) {
            return MessageResponse.builder()
                    .serviceStatus(
                            ServiceStatus.builder().success(true).build()
                    ).message("This is a cancel order").build();
        }
        return MessageResponse.builder()
                .serviceStatus(
                        ServiceStatus.builder().success(true).build()
                ).message("Successfully cancel order").build();
    }

    @PatchMapping("/orders/{id}/complete")
    public MessageResponse completeOrder(@PathVariable int id) {
        int result = this.orderService.completeOrder(id);
        String msg = "";
        if(result == 0) {
            msg = "This is a complete order";
        } else if(result == 2) {
            msg = "Order is canceled, cannot complete";
        } else{
            msg = "Successfully complete order";
        }
        return MessageResponse.builder()
                .serviceStatus(
                        ServiceStatus.builder().success(true).build()
                ).message(msg).build();
    }

    @GetMapping("/products/frequent/{count}")
    public DataResponse frequentProducts(@PathVariable int count) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        List<ProductDTO> productDTOList = this.orderService.getFrequentProducts(count, user);
        return DataResponse.builder()
                .success(true)
                .message("Successfully get requested products info")
                .data(productDTOList)
                .build();
    }

    @GetMapping("/products/recent/{count}")
    public DataResponse recentProducts(@PathVariable int count) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        List<ProductDTO> productDTOList = this.orderService.getRecentProducts(count, user);
        return DataResponse.builder()
                .success(true)
                .message("Successfully get requested products info")
                .data(productDTOList)
                .build();
    }

    @GetMapping("/products/profit/{count}")
    public DataResponse profitProducts(@PathVariable int count) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        if(user.getRole() == 0) throw new NoAuthException("You don't have permission");
        List<ProductDTO> topProducts = orderService.getProfitProducts(count);
        // HashMap<ProductDTO, Double> topProducts = orderService.getProfitProducts(count);

        return DataResponse.builder()
                .success(true)
                .message("Successfully get requested products info")
                .data(topProducts)
                .build();
    }

    @GetMapping("/products/popular/{count}")
    public DataResponse popularProducts(@PathVariable int count) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        if(user.getRole() == 0) throw new NoAuthException("You don't have permission");
        List<ProductDTO> topProducts = orderService.getPopularProducts(count);

        return DataResponse.builder()
                .success(true)
                .message("Successfully get requested products info")
                .data(topProducts)
                .build();
    }

    @GetMapping("products/{id}/sold")
    public DataResponse calculateSold(@PathVariable int id) {
        int count = this.orderService.getTotalSole(id);
        return DataResponse.builder()
                .success(true)
                .message("Successfully get requested products info")
                .data(count)
                .build();
    }

}
