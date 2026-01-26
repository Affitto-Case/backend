package com.giuseppe_tesse.turista.exception;

public class AbitazioneNotFoundException extends RuntimeException {
    public AbitazioneNotFoundException(String message) {
        super(message);
    }

    public AbitazioneNotFoundException(Long id) {
        super("Abitazione not found with id: " + id);
    }

    public AbitazioneNotFoundException(String field, String value) {
        super("Abitazione not found with " + field + ": " + value);
    }
    
}
