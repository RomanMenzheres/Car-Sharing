package com.example.carsharing.exception;

public class RentalIsNotActiveException extends RuntimeException {
    public RentalIsNotActiveException(String message) {
        super(message);
    }

    public RentalIsNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
