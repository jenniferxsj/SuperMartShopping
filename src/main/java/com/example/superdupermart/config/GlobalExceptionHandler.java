package com.example.superdupermart.config;

import com.example.superdupermart.dto.common.ErrorResponse;
import com.example.superdupermart.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;

@CrossOrigin(origins = "http://localhost:4200")
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

    @ExceptionHandler(value = {NotEnoughInventoryException.class})
    public ResponseEntity<ErrorResponse> notEnoughQuantityException(NotEnoughInventoryException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage()).build(), HttpStatus.OK);
    }
    @ExceptionHandler(value = {NoSuchOrderException.class})
    public ResponseEntity<ErrorResponse> noSuchOrderException(NoSuchOrderException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {NoAuthException.class})
    public ResponseEntity<ErrorResponse> noAuthException(NoAuthException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().statusCode(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage()).build(), HttpStatus.OK);
    }
}
