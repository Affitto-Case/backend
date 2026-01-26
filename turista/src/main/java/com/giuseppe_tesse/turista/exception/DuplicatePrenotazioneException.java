package com.giuseppe_tesse.turista.exception;

public class DuplicatePrenotazioneException extends RuntimeException {

    public DuplicatePrenotazioneException(String message) {
        super(message);
    }

    public DuplicatePrenotazioneException(String field, String value) {
        super("Duplicate prenotazione with " + field + ": " + value);
    }
    
}
