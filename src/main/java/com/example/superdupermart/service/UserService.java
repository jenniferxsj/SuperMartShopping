package com.example.superdupermart.service;

import com.example.superdupermart.dao.UserDao;
import com.example.superdupermart.domain.User;
import com.example.superdupermart.dto.user.UserCreationRequest;
import com.example.superdupermart.exception.UserAlreadyExistException;
import com.example.superdupermart.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.loadUserByUsername(username);
//        System.out.println("in userService: "+user);
//        if(user == null) {
//            throw new UsernameNotFoundException("Username does not exist");
//        }
        return AuthUserDetail.builder() // spring security's userDetail
                .username(user.getUsername())
                .password(new BCryptPasswordEncoder().encode(user.getPasswordHash()))
                .role(user.getRole())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    public void addUser(UserCreationRequest request) {
        User userEmail = userDao.loadUserByEmail(request.getEmail());
        if(userEmail != null) {
            throw new UserAlreadyExistException("Email exists, try another");
        }
        User userUsername = userDao.loadUserByUsername(request.getUsername());
        if(userUsername != null) {
            throw new UserAlreadyExistException("Username exists, try another");
        }
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(request.getPassword())
                .role(request.getRole())
                .username(request.getUsername()).build();
        userDao.addUser(user);
    }

    public User getUserByEmail(UserCreationRequest request) {
        return userDao.loadUserByEmail(request.getEmail());
    }

    public User getUserByUsername(String username) {
        return userDao.loadUserByUsername(username);
    }
}
