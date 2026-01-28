package com.giuseppe_tesse.turista.exception;

public class DuplicateFeedbackException extends RuntimeException {

    public DuplicateFeedbackException(String message) {
        super(message);
    }

    public DuplicateFeedbackException(String field, String value) {
        super("Duplicate feedback with " + field + ": " + value);
    }
    
}