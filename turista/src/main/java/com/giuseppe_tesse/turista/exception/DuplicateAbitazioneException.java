package com.giuseppe_tesse.turista.exception;

public class DuplicateAbitazioneException extends RuntimeException {
    public DuplicateAbitazioneException(String message) {
        super(message);
    }

    public DuplicateAbitazioneException(String field, String value) {
        super("Duplicate abitazione with " + field + ": " + value);
    }
    
}
