package com.giuseppe_tesse.turista.exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(String message) {
        super(message);
    }

    public BookingNotFoundException(String field, String value) {
        super("Prenotazione not found with " + field + ": " + value);
    }

    public BookingNotFoundException(long id) {
        super("Prenotazione not found with id: " + id);
    }
    
}
