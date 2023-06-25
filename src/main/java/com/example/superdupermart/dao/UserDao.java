package com.example.superdupermart.dao;

import com.example.superdupermart.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import java.util.List;

@Repository
public class UserDao extends AbstractHibernateDao<User> {

    public UserDao() {
        setClazz(User.class);
    }

    public User loadUserByUsername(String username) {
        Session session = getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        criteria.select(root).where(builder.equal(root.get("username"), username));
        Query query = session.createQuery(criteria);
        List<User> userList = query.getResultList();
        return userList == null || userList.size() == 0 ? null : userList.get(0);
    }

    public User loadUserByEmail(String email) {
        Session session = getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        criteria.select(root).where(builder.equal(root.get("email"), email));
        Query query = session.createQuery(criteria);
        List<User> userList = query.getResultList();
        return userList == null || userList.size() == 0 ? null : userList.get(0);
    }

    public void addUser(User user) {
        this.add(user);
    }
}
