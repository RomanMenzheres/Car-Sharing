package com.example.carsharing.exception;

public class RentalIsAlreadyPaid extends RuntimeException {
    public RentalIsAlreadyPaid(String message) {
        super(message);
    }

    public RentalIsAlreadyPaid(String message, Throwable cause) {
        super(message, cause);
    }
}
