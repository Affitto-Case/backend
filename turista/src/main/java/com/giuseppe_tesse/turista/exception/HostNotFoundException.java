package com.giuseppe_tesse.turista.exception;

public class HostNotFoundException extends RuntimeException{

     public HostNotFoundException(String message) {
        super(message);
    }

    public HostNotFoundException(String field, String value) {
        super("User not found with " + field + ": " + value);
    }

    public HostNotFoundException(long id) {
        super("User not found with id: " + id);
    }
    
}
