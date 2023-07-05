package com.example.superdupermart.exception;

public class NoSuchOrderException extends RuntimeException {
    private String message;
    public NoSuchOrderException(String message) {
        super(message);
        this.message = message;
    }
}
