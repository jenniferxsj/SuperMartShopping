package com.example.superdupermart.exception;

public class NotEnoughInventoryException extends RuntimeException {
    private String message;
    public NotEnoughInventoryException(String message) {
        super(message);
        this.message = message;
    }
}
