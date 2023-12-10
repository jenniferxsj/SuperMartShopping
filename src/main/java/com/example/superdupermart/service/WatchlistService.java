package com.example.superdupermart.service;

import com.example.superdupermart.dao.ProductDao;
import com.example.superdupermart.dao.WatchlistDao;
import com.example.superdupermart.domain.Product;
import com.example.superdupermart.domain.User;
import com.example.superdupermart.domain.Watchlist;
import com.example.superdupermart.dto.product.ProductDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.PostConstruct;
import java.util.*;
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

    public List<ProductDTO> getUserWatchlist(User user) {
        return watchlistDao.getUserAllWatchlist(user).stream()
                .filter(watchlist -> watchlist.getProduct().getQuantity() > 0)
                .map(watchlist -> ProductDTO.builder()
                        .id(watchlist.getProduct().getId())
                        .name(watchlist.getProduct().getName())
                        .description(watchlist.getProduct().getDescription())
                        .build())
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
