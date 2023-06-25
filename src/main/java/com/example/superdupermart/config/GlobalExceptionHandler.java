package com.example.superdupermart.config;

import com.example.superdupermart.dto.common.ErrorResponse;
import com.example.superdupermart.exception.InvalidCredentialsException;
import com.example.superdupermart.exception.NoSuchUserExistsException;
import com.example.superdupermart.exception.UserAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {NoSuchUserExistsException.class})
    public ResponseEntity<ErrorResponse> noUserExistException(NoSuchUserExistsException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {UserAlreadyExistException.class})
    public ResponseEntity<ErrorResponse> userExistException(UserAlreadyExistException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {InvalidCredentialsException.class})
    public ResponseEntity<ErrorResponse> invalidCredentialsException(InvalidCredentialsException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().statusCode(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage()).build(), HttpStatus.OK);
    }
}
