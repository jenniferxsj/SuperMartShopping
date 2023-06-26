package com.example.superdupermart.dao;

import com.example.superdupermart.domain.Order;
import com.example.superdupermart.domain.OrderItem;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractHibernateDao<OrderItem> {
    public OrderItemDao() {
        setClazz(OrderItem.class);
    }

    public List<OrderItem> getAllOrderItemsInSameOrder(Order order) {
        Session session = getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<OrderItem> criteria = builder.createQuery(OrderItem.class);
        Root<OrderItem> root = criteria.from(OrderItem.class);
        criteria.select(root).where(builder.equal(root.get("order"), order));
        Query query = session.createQuery(criteria);
        List<OrderItem> orderItemList = query.getResultList();
        return orderItemList;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.add(orderItem);
    }

    public void updateOrderItemQuantity(OrderItem item) {
        Session session = getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        // create update
        CriteriaUpdate<OrderItem> update = builder.createCriteriaUpdate(OrderItem.class);
        // set the root class
        Root<OrderItem> root = update.from(OrderItem.class);
        update.set("quantity", item.getQuantity());
        update.where(builder.equal(root.get("id"), item.getId()));
        session.createQuery(update).executeUpdate();
    }
}
