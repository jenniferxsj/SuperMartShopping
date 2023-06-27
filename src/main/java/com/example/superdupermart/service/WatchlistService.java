package com.example.superdupermart.service;

import com.example.superdupermart.dao.ProductDao;
import com.example.superdupermart.dao.WatchlistDao;
import com.example.superdupermart.domain.Product;
import com.example.superdupermart.domain.User;
import com.example.superdupermart.domain.Watchlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class WatchlistService {
    private final WatchlistDao watchlistDao;
    private final ProductDao productDao;

    @Autowired
    public WatchlistService(WatchlistDao watchlistDao, ProductDao productDao) {
        this.watchlistDao = watchlistDao;
        this.productDao = productDao;
    }

    public int createWatchlist(User user, int product_id) {
        Product product = productDao.getProductById(product_id);
        Optional<Watchlist> exist = watchlistDao.getUserAllWatchlist(user).stream()
                .filter(watchlist -> watchlist.getProduct().getId() == product.getId())
                .findAny();
        if(!exist.isPresent()) {
            watchlistDao.addWatchlist(Watchlist.builder()
                    .user(user)
                    .product(product).build());
            return 1;
        }
        return 0;
    }

    public List<Watchlist> getUserWatchlist(User user) {
        return watchlistDao.getUserAllWatchlist(user).stream()
                .filter(watchlist -> watchlist.getProduct().getQuantity() > 0)
                .collect(Collectors.toList());
    }

    public int deleteWatchlist(User user, int product_id) {
        Optional<Watchlist> exist = watchlistDao.getUserAllItems(user).stream()
                .filter(list -> list.getProduct().getId() == product_id)
                .findAny();
        if(!exist.isPresent()) return 0;
        watchlistDao.deleteWatchlistById(exist.get().getId());
        return 1;
    }
}
