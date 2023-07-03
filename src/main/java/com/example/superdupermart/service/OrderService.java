package com.example.superdupermart.service;

import com.example.superdupermart.dao.OrderDao;
import com.example.superdupermart.dao.OrderItemDao;
import com.example.superdupermart.dao.ProductDao;
import com.example.superdupermart.dao.UserDao;
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
import java.util.*;

@Service
@Transactional
public class OrderService {
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final ProductDao productDao;
    private final UserDao userDao;


    @Autowired
    public OrderService(OrderDao orderDao, OrderItemDao orderItemDao, ProductDao productDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.productDao = productDao;
        this.userDao = userDao;
    }

    public void placeOrder(User user, CreateOrderRequest createOrderRequest) {
        Timestamp time_created = new Timestamp(System.currentTimeMillis());
        int order_id = orderDao.addOrder(Order.builder()
                .date_placed(time_created)
                .order_status("Processing")
                .user(user).build()
        );
        Order order = orderDao.getOrderById(order_id);

        for(ProductRequest newOrder : createOrderRequest.getOrder()) {
            Product product = productDao.getProductById(newOrder.getProductId());
            int num = newOrder.getQuantity();
            if (num > product.getQuantity()) {
                throw new NotEnoughInventoryException("Not enough this product in stock, sorry");
            }
            product.setQuantity(product.getQuantity() - num);
            orderItemDao.addOrderItem(OrderItem.builder()
                    .purchased_price(product.getRetail_price())
                    .quantity(num)
                    .wholesale_price(product.getWholesale_price())
                    .order(order)
                    .product(product)
                    .build());
        }
    }
    public Optional<OrderItem> getOrderItem(Order order, Product product) {
        return orderItemDao.getAllOrderItemsInSameOrder(order).stream()
                .filter(item -> item.getProduct() == product)
                .findAny();
    }

    public Optional<Order> findUserProcessingOrder(User user) {
        return orderDao.getUserAllOrder(user).stream()
                .filter(order -> order.getOrder_status().equals("Processing"))
                .findAny();
    }

    public List<Order> getUserAllOrders(User user) {
        return orderDao.getUserAllOrder(user);
    }

    public Order getOrderById(int id) {
        return orderDao.getOrderById(id);
    }

    public void submitOrder(int id) {
        Order order = orderDao.getOrderById(id);
        List<OrderItem> orderItemList = orderItemDao.getAllOrderItemsInSameOrder(order);
        for(OrderItem item : orderItemList) {
            Product product = productDao.getProductById(item.getProduct().getId());
            product.setQuantity(product.getQuantity() - item.getQuantity());
        }
        order.setOrder_status("Completed");
    }

    public List<List<Object>> getAllOrders() {
        List<Order> orderList = orderDao.getAllOrder();
        Collections.sort(orderList, (o1, o2) -> o2.getDate_placed().compareTo(o1.getDate_placed()));
        List<List<Object>> orderWithUser = new ArrayList<>();
        for(Order order : orderList) {
            List<Object> list = new ArrayList<>();
            list.add(order);
            list.add(order.getUser().getUsername());
            orderWithUser.add(list);
        }
        return orderWithUser;
    }
}
