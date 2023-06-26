package com.example.superdupermart.service;

import com.example.superdupermart.dao.OrderDao;
import com.example.superdupermart.dao.OrderItemDao;
import com.example.superdupermart.dao.ProductDao;
import com.example.superdupermart.domain.Order;
import com.example.superdupermart.domain.OrderItem;
import com.example.superdupermart.domain.Product;
import com.example.superdupermart.domain.User;
import com.example.superdupermart.dto.order.CreateOrderRequest;
import com.example.superdupermart.dto.product.ProductRequest;
import com.example.superdupermart.exception.NotEnoughInventoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final ProductDao productDao;


    @Autowired
    public OrderService(OrderDao orderDao, OrderItemDao orderItemDao, ProductDao productDao) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.productDao = productDao;
    }

    public void placeOrder(User user, CreateOrderRequest createOrderRequest) {
        // check where there is incomplete order. If yes, use that; otherwise, create a new one
        Optional<Order> orderExist = findUserProcessingOrder(user.getId());
        Order order;
        if(!orderExist.isPresent()) {
            Timestamp time_created = new Timestamp(System.currentTimeMillis());
            orderDao.addOrder(Order.builder()
                    .date_placed(time_created)
                    .order_status("Processing")
                    .user(user).build()
            );
            order = findUserProcessingOrder(user.getId()).get();
        } else {
            order = orderExist.get();
        }

        for(ProductRequest newOrder : createOrderRequest.getOrder()) {
            Product product = productDao.getProductById(newOrder.getProductId());
            Optional<OrderItem> orderItem = getOrderItem(order, product);
            if(orderItem.isPresent()) {
                OrderItem item = orderItem.get();
                int num = orderItem.get().getQuantity() + newOrder.getQuantity();
                if(num > product.getQuantity())
                    throw new NotEnoughInventoryException("Not enough this product in stock, sorry");
                item.setQuantity(num);
                orderItemDao.updateOrderItemQuantity(item);
            } else {
                int num = newOrder.getQuantity();
                if (num > product.getQuantity())
                    throw new NotEnoughInventoryException("Not enough this product in stock, sorry");

                orderItemDao.addOrderItem(OrderItem.builder()
                        .purchased_price(product.getRetail_price())
                        .quantity(num)
                        .wholesale_price(product.getWholesale_price())
                        .order(order)
                        .product(product)
                        .build());
            }
        }
    }
    public Optional<OrderItem> getOrderItem(Order order, Product product) {
        return orderItemDao.getAllOrderItemsInSameOrder(order).stream()
                .filter(item -> item.getProduct() == product)
                .findAny();
    }

    public Optional<Order> findUserProcessingOrder(int user_id) {
        return orderDao.getUserAllOrder(user_id).stream()
                .filter(order -> order.getOrder_status().equals("Processing"))
                .findAny();
    }
}
