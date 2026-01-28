package com.giuseppe_tesse.turista.exception;

public class DuplicateBookingException extends RuntimeException {

    public DuplicateBookingException(String message) {
        super(message);
    }

    public DuplicateBookingException(String field, String value) {
        super("Duplicate prenotazione with " + field + ": " + value);
    }
    
}
