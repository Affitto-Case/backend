package com.giuseppe_tesse.turista.service;

import java.util.List;

import com.giuseppe_tesse.turista.dao.FeedbackDAO;
import com.giuseppe_tesse.turista.model.Feedback;
import com.giuseppe_tesse.turista.model.Prenotazione;
import com.giuseppe_tesse.turista.exception.FeedbackNotFoundException;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FeedbackService {

    private final FeedbackDAO feedbackDAO;

    public FeedbackService(FeedbackDAO feedbackDAO) {
        this.feedbackDAO = feedbackDAO;
    }

// ==================== CREATE ====================

    public Feedback insertFeedback(Prenotazione prenotazione, String titolo, int valutazione, String commento) {
        log.info("Tentativo di inserimento feedback - Prenotazione ID: {}, Titolo: {}", prenotazione.getId(), titolo);
        Feedback feedback = new Feedback(prenotazione, titolo, valutazione, commento);
        return feedbackDAO.create(feedback);
    }

// ==================== READ ====================

    public Feedback getFeedbackById(Long id) {
        log.info("Ricerca feedback per ID: {}", id);
        return feedbackDAO.findById(id).orElseThrow(() -> {
            log.warn("Feedback non trovato con ID: {}", id);
            return new FeedbackNotFoundException(id);
        });
    }

    public List<Feedback> getAllFeedbacks() {
        log.info("Recupero di tutti i feedback");
        return feedbackDAO.findAll();
    }

    public List<Feedback> getFeedbacksByUtente(Long utente_id) {
        log.info("Ricerca feedback per ID utente: {}", utente_id);
        return feedbackDAO.findByUtente(utente_id).orElseThrow(() -> {
            log.warn("Nessun feedback trovato per ID utente: {}", utente_id);
            return new FeedbackNotFoundException("Nessun feedback trovato per ID utente: " + utente_id);
        });
    }

    public List<Feedback> getFeedbacksByStruttura(Long prenotazione_id) {
        log.info("Ricerca feedback per ID prenotazione: {}", prenotazione_id);
        return feedbackDAO.findByStruttura(prenotazione_id).orElseThrow(() -> {
            log.warn("Nessun feedback trovato per ID prenotazione: {}", prenotazione_id);
            return new FeedbackNotFoundException("Nessun feedback trovato per ID prenotazione: " + prenotazione_id);
        });
    }

    public Feedback getFeedbackByUtenteAndPrenotazione(Long utente_id, Long prenotazione_id) {
        log.info("Ricerca feedback per ID utente: {} e ID prenotazione: {}", utente_id, prenotazione_id);
        return feedbackDAO.findByUtenteIdAndPrenotazioneId(utente_id, prenotazione_id).orElseThrow(() -> {
            log.warn("Feedback non trovato per ID utente: {} e ID prenotazione: {}", utente_id, prenotazione_id);
            return new FeedbackNotFoundException("Feedback non trovato per ID utente: " + utente_id +
                                                " e ID prenotazione: " + prenotazione_id);
        });
    }

// ==================== UPDATE ====================

    public Feedback updateFeedbackComment(Feedback feedback, String newComment) {
        log.info("Aggiornamento commento feedback ID: {}", feedback.getId());
        feedback.setCommento(newComment);
        return feedbackDAO.updateCommento(feedback).orElseThrow(() -> {
            log.warn("Aggiornamento commento fallito per feedback ID: {}", feedback.getId());
            return new FeedbackNotFoundException(feedback.getId());
        });
    }

// ==================== DELETE ====================

    public int deleteAllFeedbacks() {
        log.info("Eliminazione di tutti i feedback");
        return feedbackDAO.deleteAll();
    }

    public boolean deleteFeedbackById(Long id) {
        log.info("Tentativo di eliminazione feedback con ID: {}", id);
        boolean deleted = feedbackDAO.deleteById(id);
        if (!deleted) {
            log.warn("Eliminazione feedback fallita - Feedback non trovato con ID: {}", id);
            throw new FeedbackNotFoundException(id);
        }
        return true;
    }

    public boolean deleteFeedback(Long utente_id, Long prenotazione_id) {
        log.info("Tentativo di eliminazione feedback per ID utente: {} e ID prenotazione: {}", utente_id, prenotazione_id);
        boolean deleted = feedbackDAO.delete(utente_id, prenotazione_id);
        if (!deleted) {
            log.warn("Eliminazione feedback fallita - Feedback non trovato per ID utente: {} e ID prenotazione: {}", utente_id, prenotazione_id);
            throw new FeedbackNotFoundException("Feedback non trovato per ID utente: " + utente_id +
                                                " e ID prenotazione: " + prenotazione_id);
        }
        return true;
    }
    
}
