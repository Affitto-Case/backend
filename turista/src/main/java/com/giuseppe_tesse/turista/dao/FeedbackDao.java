package com.giuseppe_tesse.turista.dao;

import java.util.List;
import java.util.Optional;
import com.giuseppe_tesse.turista.model.Feedback;

public class FeedbackDao {

    public interface FeedbackDAO{

// ==================== CREATE ====================

    Feedback create(Feedback feedback);

// ==================== READ ====================

    List<Feedback> readAll();

    Optional<List<Feedback>> readByUtente(Long utente_id);

    Optional<List<Feedback>> readByStruttura(Long prenotazione_id);

    Optional<Feedback> readById(Long idFeedback);

    Optional<Feedback> read(Long utente_id, Long prenotazione_id);

// ==================== UPDATE ====================

    Optional<Feedback> updateCommento(Feedback feedback);

// ==================== DELETE ====================

    int deleteAll();

    boolean deleteById(Long idFeedback);

    boolean delete(Long utente_id, Long prenotazione_id);

    }
    
}
