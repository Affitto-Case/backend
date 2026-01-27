package com.giuseppe_tesse.turista.dao;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.model.Abitazione;

public interface AbitazioneDAO{

// ==================== CREATE ====================

    Abitazione create(Abitazione abitazione);

// ==================== READ ====================

    List<Abitazione> findAll();

    List<Abitazione> findByProprietario(Long proprietario_id);

    Optional<Abitazione> findById(Long id);
    
    Optional<Abitazione> findByIndirizzoAndPiano(String indirizzo, int piano);

// ==================== UPDATE ====================

    Optional<Abitazione> update(Abitazione abitazione);

// ==================== DELETE ====================

    int deleteAll();

    boolean deleteById(Long id);

    boolean deleteByProprietario(Long proprietario_id);

    }
