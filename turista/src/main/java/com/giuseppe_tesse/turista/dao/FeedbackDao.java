package com.giuseppe_tesse.turista.dao;

import java.util.List;
import java.util.Optional;
import com.giuseppe_tesse.turista.model.Feedback;

public interface FeedbackDAO {

// ==================== CREATE ====================

    Feedback create(Feedback feedback);

// ==================== READ ====================

    List<Feedback> findAll();

    // Ricerca feedback lasciati da uno specifico utente
    List<Feedback> findByUserId(Long userId);

    // Ricerca feedback legati a una specifica prenotazione
    Optional<List<Feedback>> findByBookingId(Long bookingId);

    Optional<Feedback> findById(Long feedbackId);

    // Ricerca il feedback specifico di un utente per una determinata prenotazione
    Optional<Feedback> findByUserIdAndBookingId(Long userId, Long bookingId);

// ==================== UPDATE ====================

    // Aggiorna il commento di un feedback esistente
    Optional<Feedback> updateComment(Feedback feedback);

// ==================== DELETE ====================

    int deleteAll();

    boolean deleteById(Long feedbackId);

    // Elimina basandosi sulla combinazione utente e prenotazione
    boolean deleteByUserIdAndBookingId(Long userId, Long bookingId);
}