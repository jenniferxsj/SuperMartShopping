package com.example.superdupermart.dao;

import com.example.superdupermart.domain.User;
import com.example.superdupermart.domain.Watchlist;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WatchlistDao extends AbstractHibernateDao<Watchlist> {
    public WatchlistDao() {
        setClazz(Watchlist.class);
    }

    public Watchlist getWatchlistById(int id) {
        return this.findById(id);
    }

    public void addWatchlist(Watchlist watchlist) {
        this.add(watchlist);
    }

    public List<Watchlist> getUserAllWatchlist(User user) {
        return getUserAllItems(user);
    }

    public void deleteWatchlistById(Integer id) {
        Session session = getCurrentSession();
        String hql = "delete from Watchlist where id = :id";
        session.createQuery(hql).setInteger("id", id).executeUpdate();
    }
}
