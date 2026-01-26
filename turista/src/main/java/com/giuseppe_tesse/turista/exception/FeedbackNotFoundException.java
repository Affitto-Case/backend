package com.giuseppe_tesse.turista.exception;

public class FeedbackNotFoundException extends RuntimeException {
    public FeedbackNotFoundException(String message) {
        super(message);
    }

    public FeedbackNotFoundException(Long id) {
        super("Feedback non trovato con ID: " + id);
    }

    public FeedbackNotFoundException(Long utente_id, Long prenotazione_id) {
        super("Feedback non trovato per Utente ID: " + utente_id + " e Prenotazione ID: " + prenotazione_id);
    }

    public FeedbackNotFoundException(String field, String value) {
        super("Feedback non trovato con " + field + ": " + value);
    }
    
}
