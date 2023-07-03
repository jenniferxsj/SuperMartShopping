package com.example.superdupermart.dao;

import com.example.superdupermart.domain.Order;
import com.example.superdupermart.domain.OrderItem;
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

    public int addOrder(Order order) {
        return this.add(order);
    }

    public List<Order> getUserAllOrder(User user) {
        Session session = getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> root = criteria.from(Order.class);
        criteria.select(root).where(builder.equal(root.get("user"), user));
        Query query = session.createQuery(criteria);
        List<Order> orderList = query.getResultList();
        return orderList;
    }
}
