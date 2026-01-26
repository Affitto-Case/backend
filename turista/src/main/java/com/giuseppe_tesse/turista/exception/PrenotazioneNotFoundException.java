package com.giuseppe_tesse.turista.exception;

public class PrenotazioneNotFoundException extends RuntimeException {

    public PrenotazioneNotFoundException(String message) {
        super(message);
    }

    public PrenotazioneNotFoundException(String field, String value) {
        super("Prenotazione not found with " + field + ": " + value);
    }

    public PrenotazioneNotFoundException(long id) {
        super("Prenotazione not found with id: " + id);
    }
    
}
