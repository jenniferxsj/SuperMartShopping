package com.example.superdupermart.controller;

import com.example.superdupermart.dao.WatchlistDao;
import com.example.superdupermart.domain.User;
import com.example.superdupermart.dto.common.DataResponse;
import com.example.superdupermart.dto.common.MessageResponse;
import com.example.superdupermart.dto.common.ServiceStatus;
import com.example.superdupermart.service.UserService;
import com.example.superdupermart.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;
    private final UserService userService;
    @Autowired
    public WatchlistController(WatchlistService watchlistService, UserService userService) {
        this.watchlistService = watchlistService;
        this.userService = userService;
    }

    @PostMapping("/product/{id}")
    public MessageResponse addToWatchlist(@PathVariable int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        int addNewList = watchlistService.createWatchlist(user, id);
        if(addNewList == 0) {
            return MessageResponse.builder().serviceStatus(
                    ServiceStatus.builder().success(false).build()
            ).message("Watchlist exists").build();
        }
        return  MessageResponse.builder().serviceStatus(
                ServiceStatus.builder().success(true).build()
        ).message("Successfully added to watchlist").build();
    }

    @GetMapping("/products/all")
    public DataResponse getUserAllWatchList() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());

        return DataResponse.builder()
                .success(true)
                .message("Successfully get user watchlist")
                .data(watchlistService.getUserWatchlist(user))
                .build();
    }

    @DeleteMapping("/product/{id}")
    public MessageResponse deleteWatchlist(@PathVariable int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        int succeed = watchlistService.deleteWatchlist(user, id);
        if(succeed == 0) {
            return MessageResponse.builder().serviceStatus(
                    ServiceStatus.builder().success(false).build()
            ).message("Watchlist does not exist").build();
        }
        return  MessageResponse.builder().serviceStatus(
                ServiceStatus.builder().success(true).build()
        ).message("Successfully delete the watchlist").build();
    }
}
