package com.example.superdupermart.controller;

import com.example.superdupermart.domain.User;
import com.example.superdupermart.dto.common.MessageResponse;
import com.example.superdupermart.dto.common.ServiceStatus;
import com.example.superdupermart.dto.login.LoginRequest;
import com.example.superdupermart.dto.login.LoginResponse;
import com.example.superdupermart.dto.user.UserCreationRequest;
import com.example.superdupermart.exception.InvalidCredentialsException;
import com.example.superdupermart.security.AuthUserDetail;
import com.example.superdupermart.security.JwtProvider;
import com.example.superdupermart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;
    private UserService userService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, JwtProvider jwtProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

    //User trying to log in with username and password
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        Authentication authentication;
        //Try to authenticate the user using the username and password
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e){
            throw new InvalidCredentialsException("Incorrect credentials, please try again.");
        }

        //Successfully authenticated user will be stored in the authUserDetail object
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal(); //getPrincipal() returns the user object

        //A token wil be created using the username/email/userId and permission
        String token = jwtProvider.createToken(authUserDetail);

        //Returns the token as a response to the frontend/postman
        return LoginResponse.builder()
                .message("Welcome " + authUserDetail.getUsername())
                .token(token)
                .build();
    }

    @PostMapping("/signup")
    public MessageResponse createUser(@RequestBody UserCreationRequest request) {
        userService.addUser(request);
        return MessageResponse.builder()
                .serviceStatus(
                        ServiceStatus.builder().success(true).build()
                ).message("New User Created").build();
    }
}
