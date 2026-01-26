package com.giuseppe_tesse.turista.dao;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.model.Abitazione;
import com.giuseppe_tesse.turista.model.Prenotazione;

public interface PrenotazioneDAO {

// ==================== CREATE ====================

    Prenotazione create(Prenotazione prenotazione);

// ==================== READ ====================

    Optional<Prenotazione> findById(Long id);

    Optional<Abitazione> findByAbitazioneId(Long abitazioneId);

    Optional<List<Prenotazione>> findByPrenotazioneId(Long abitazioneId);

    List<Prenotazione> findAll();

// ==================== UPDATE ====================

    Optional<Prenotazione> update(Prenotazione prenotazione);

// ==================== DELETE ====================

    boolean deleteById(Long id);

    int deleteAll();

    }
