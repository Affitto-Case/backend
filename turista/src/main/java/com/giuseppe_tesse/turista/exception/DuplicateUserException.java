package com.giuseppe_tesse.turista.exception;

/**
 * Eccezione custom lanciata quando si tenta di creare un utente
 * con email o username già esistenti (violazione vincolo UNIQUE).
 */
public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String message) {
        super(message);
    }

    public DuplicateUserException(String field, String value) {
        super("User already exists with " + field + ": " + value);
    }

}
