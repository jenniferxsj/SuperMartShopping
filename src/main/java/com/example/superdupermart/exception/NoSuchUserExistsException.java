package com.example.superdupermart.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoSuchUserExistsException extends RuntimeException{
    private String message;
    public NoSuchUserExistsException(String message) {
        super(message);
        this.message = message;
    }
}
