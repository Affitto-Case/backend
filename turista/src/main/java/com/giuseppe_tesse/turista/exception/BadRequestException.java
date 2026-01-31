package com.giuseppe_tesse.turista.exception;

public class BadRequestException extends RuntimeException {

    private final int statusCode = 400;

    public BadRequestException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
