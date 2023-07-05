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
import com.example.superdupermart.exception.NoSuchOrderException;
import com.example.superdupermart.exception.NotEnoughInventoryException;
import org.springframework.beans.factory.annotation.Autowired;
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
        return orderDao.getUserAllOrder(user);
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

    public List<List<Object>> getAllOrders2() {
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

    public List<Order> getAllOrder() {
        List<Order> orderList = orderDao.getAllOrder();
        return orderList;
    }

    public int cancelOrder(int id) {
        Order order = getOrderById(id);
        if(order == null) {
            throw new NoSuchOrderException("The order does not exist");
        }
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

    public int completeOrder(int id) {
        Order order = getOrderById(id);
        if(order == null) {
            throw new NoSuchOrderException("The order does not exist");
        }
        if(order.getOrder_status().equals("Completed")) {
            return 0;
        } else if(order.getOrder_status().equals("Canceled")) {
            return 2;
        }
        order.setOrder_status("Completed");
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
                }).limit(count).map(entry -> {
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

    public List<ProductDTO> getPopularProducts(int count) {
        List<Order> orderList = this.orderDao.getAllOrder();
        List<ProductDTO> topProducts = orderList.stream()
                .flatMap(order -> order.getOrderItemList().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getProduct,
                        Collectors.summingInt(OrderItem::getQuantity)))
                .entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .limit(3)
                .map(entry -> {
                    Product product = entry.getKey();
                    return ProductDTO.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription()).build();
                })
                .collect(Collectors.toList());
        return topProducts;
    }

    public List<ProductDTO> getProfitProducts(int count) {
        List<Order> orderList = this.orderDao.getAllOrder().stream()
                .filter(order -> order.getOrder_status().equals("Completed"))
                .collect(Collectors.toList());
        List<ProductDTO> topProducts = orderList.stream()
                .flatMap(order -> order.getOrderItemList().stream())
                .map(orderItem -> new AbstractMap.SimpleEntry<>(
                        orderItem.getProduct(),
                        (orderItem.getProduct().getRetail_price() - orderItem.getProduct().getWholesale_price()) * orderItem.getQuantity())
                )
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey, Collectors.summingDouble(AbstractMap.SimpleEntry::getValue)))
                .entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
                .limit(count)
                .map(entry -> {
                    Product product = entry.getKey();
                    return ProductDTO.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription()).build();
                })
                .collect(Collectors.toList());
        return topProducts;
    }

    public int getTotalSole(int id) {
        Product product = this.productDao.getProductById(id);
        System.out.println(product);
        System.out.println("id:"+ id);
        int count = 0;
        List<OrderItem> orderList = this.orderDao.getAllOrder().stream()
                .filter(order -> order.getOrder_status().equals("Completed"))
                .flatMap(order -> order.getOrderItemList().stream())
                .filter(item -> item.getProduct().getId() == product.getId())
                .collect(Collectors.toList());
        for(OrderItem orderItem: orderList) {
            count += orderItem.getQuantity();
        }
        return count;
    }

    public List<Order> getAllOrdersPageable(int pageNumber, int pageSize) {
        return orderDao.getAllOrderPageable(pageNumber, pageSize);
    }
}
