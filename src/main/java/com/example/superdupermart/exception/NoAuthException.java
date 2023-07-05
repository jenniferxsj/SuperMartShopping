package com.example.superdupermart.exception;

public class NoAuthException extends RuntimeException{
    private String message;
    public NoAuthException(String message) {
        super(message);
        this.message = message;
    }
}
