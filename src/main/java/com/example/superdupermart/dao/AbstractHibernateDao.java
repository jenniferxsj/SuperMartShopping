package com.example.superdupermart.dao;

import com.example.superdupermart.domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractHibernateDao<T> {
    @Autowired
    protected SessionFactory sessionFactory;

    protected Class<T> clazz;

    protected final void setClazz(final Class<T> clazzToSet) {
        clazz = clazzToSet;
    }

    public List<T> getAll() {
        Session session = getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(clazz);
        criteria.from(clazz);
        return session.createQuery(criteria).getResultList();
    }

    public T findById(int id) {
        return getCurrentSession().get(clazz, id);
    }

    public int add(T item) {
        return (int)getCurrentSession().save(item);
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<T> getUserAllItems(User user) {
        Session session = getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(clazz);
        Root<T> root = criteria.from(clazz);
        criteria.select(root).where(builder.equal(root.get("user"), user));
        Query query = session.createQuery(criteria);
        return (List<T>) query.getResultList();
    }

}
