package com.giuseppe_tesse.turista.exception;

public class ResidenceNotFoundException extends RuntimeException {
    public ResidenceNotFoundException(String message) {
        super(message);
    }

    public ResidenceNotFoundException(Long id) {
        super("Abitazione not found with id: " + id);
    }

    public ResidenceNotFoundException(String field, String value) {
        super("Abitazione not found with " + field + ": " + value);
    }
    
}
