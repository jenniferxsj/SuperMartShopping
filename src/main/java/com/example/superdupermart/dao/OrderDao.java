package com.example.superdupermart.dao;

import com.example.superdupermart.domain.Order;
import com.example.superdupermart.domain.User;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderDao extends AbstractHibernateDao<Order> {
    public OrderDao() {
        setClazz(Order.class);
    }

    public Order getOrderById(int id) {
        return this.findById(id);
    }

    public List<Order> getAllOrder() {
        return this.getAll();
    }

    public void addOrder(Order order) {
        this.add(order);
    }

    public List<Order> getUserAllOrder(int user_id) {
        List<Order> orderList = this.getAll()
                .stream()
                .filter(order -> order.getUser().getId() == user_id)
                .collect(Collectors.toList());
        return orderList;
    }
}
