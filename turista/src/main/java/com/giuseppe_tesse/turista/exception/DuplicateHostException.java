package com.giuseppe_tesse.turista.exception;

public class DuplicateHostException extends RuntimeException{

    public DuplicateHostException(String message) {
        super(message);
    }

    public DuplicateHostException(String field, String value) {
        super("Host already exists with " + field + ": " + value);
    }

    
}
