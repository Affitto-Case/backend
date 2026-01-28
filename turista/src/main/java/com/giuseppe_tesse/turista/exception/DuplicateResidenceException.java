package com.giuseppe_tesse.turista.exception;

public class DuplicateResidenceException extends RuntimeException {
    public DuplicateResidenceException(String message) {
        super(message);
    }

    public DuplicateResidenceException(String field, String value) {
        super("Duplicate abitazione with " + field + ": " + value);
    }
    
}
