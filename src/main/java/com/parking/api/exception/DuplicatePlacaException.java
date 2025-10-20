package com.parking.api.exception;

public class DuplicatePlacaException extends RuntimeException {

    public DuplicatePlacaException(String message) {
        super(message);
    }
}
