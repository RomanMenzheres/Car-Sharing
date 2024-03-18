package com.example.carsharing.exception;

public class NoAvailableCarsException extends RuntimeException {
    public NoAvailableCarsException(String message) {
        super(message);
    }

    public NoAvailableCarsException(String message, Throwable cause) {
        super(message, cause);
    }
}
