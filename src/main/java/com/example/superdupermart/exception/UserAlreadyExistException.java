package com.example.superdupermart.exception;

import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserAlreadyExistException extends RuntimeException {
    private String message;
    public UserAlreadyExistException(String message) {
        super(message);
        this.message = message;
    }

}
