package com.giuseppe_tesse.turista.dao;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.model.Prenotazione;

public class PrenotazioneDao {

    public interface PrenotazioneDAO {

// ==================== CREATE ====================

    Prenotazione create(Prenotazione prenotazione);

// ==================== READ ====================

    Optional<Prenotazione> findById(Long id);

    List<Prenotazione> findAll();

// ==================== UPDATE ====================

    Optional<Prenotazione> update(Prenotazione prenotazione);

// ==================== DELETE ====================

    boolean deleteById(Long id);

    int deleteAll();

    }
    
}
