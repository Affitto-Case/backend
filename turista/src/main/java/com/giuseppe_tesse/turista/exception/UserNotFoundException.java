package com.giuseppe_tesse.turista.exception;

/**
 * Eccezione custom lanciata quando un utente non viene trovato nel database.
 * Estende RuntimeException (unchecked) per non obbligare la gestione esplicita.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String field, String value) {
        super("User not found with " + field + ": " + value);
    }

    public UserNotFoundException(long id) {
        super("User not found with id: " + id);
    }

}
