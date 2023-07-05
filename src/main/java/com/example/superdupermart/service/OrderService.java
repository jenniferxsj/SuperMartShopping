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
import com.example.superdupermart.dto.product.ProductDTO;
import com.example.superdupermart.dto.product.ProductRequest;
import com.example.superdupermart.exception.NotEnoughInventoryException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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
        return orderDao.getUserAllOrder(user).stream().sorted(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return o1.getDate_placed().compareTo(o2.getDate_placed());
            }
        }).collect(Collectors.toList());
    }

    public Order getOrderById(int id) {
        Order order = orderDao.getOrderById(id);

        return order;
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
        List<List<Object>> orderWithUser = new ArrayList<>();
        for(Order order : orderList) {
            List<Object> list = new ArrayList<>();
            list.add(order);
            list.add(order.getUser().getUsername());
            orderWithUser.add(list);
        }
        return orderWithUser;
    }

    public int cancelOrder(int id) {
        Order order = getOrderById(id);
        if(order.getOrder_status().equals("Completed")) {
            return 0;
        } else if(order.getOrder_status().equals("Canceled")) {
            return 2;
        }
        order.getOrderItemList().forEach(item -> {
            Product product = item.getProduct();
            int quantity = product.getQuantity();
            product.setQuantity(quantity + item.getQuantity());
        });
        order.setOrder_status("Canceled");
        return 1;
    }

    public List<ProductDTO> getFrequentProducts(int count, User user) {
        List<Order> orderList = getUserAllOrders(user).stream()
                .filter(order -> !order.getOrder_status().equals("Canceled"))
                .collect(Collectors.toList());
        Map<Product, Long> productFrequency = orderList.stream()
                .flatMap(order -> order.getOrderItemList().stream())
                .collect(Collectors.groupingBy(OrderItem::getProduct, Collectors.counting()));
        List<ProductDTO> res = productFrequency.entrySet().stream()
                .sorted((p1, p2) -> {
                    int compareFrequency = p2.getValue().compareTo(p1.getValue());
                    if (compareFrequency != 0) {
                        return compareFrequency; // If frequencies are different, sort by frequency
                    } else {
                        return p1.getKey().getId().compareTo(p2.getKey().getId()); // If frequencies are equal, sort by product ID
                    }
                })
                .limit(count)
                .map(entry -> {
                    Product product = entry.getKey();
                    ProductDTO productDTO = ProductDTO.builder().id(product.getId())
                            .description(product.getDescription())
                            .name(product.getName()).build();
                    return productDTO;
                }).collect(Collectors.toList());
        return res;
    }

    public List<ProductDTO> getRecentProducts(int count, User user) {
        List<OrderItem> orderItemList = getUserAllOrders(user).stream()
                .filter(order -> !order.getOrder_status().equals("Canceled"))
                .flatMap(order -> order.getOrderItemList().stream())
                .sorted((item1, item2) -> item2.getOrder().getDate_placed().compareTo(item1.getOrder().getDate_placed()))
                .collect(Collectors.toList());

        List<Product> productList = new ArrayList<>();
        for(OrderItem item : orderItemList) {
            Product product = item.getProduct();
            if (!productList.contains(product)) {  // check if the product is already in the list
                productList.add(product);
            }
            if (productList.size() == count) {  // stop when we have 3 unique products
                break;
            }
        }

        List<ProductDTO> productDTOList = new ArrayList<>();
        for(Product product : productList) {
            productDTOList.add(ProductDTO.builder().id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription()).build());
        }
        return productDTOList;
    }
}
