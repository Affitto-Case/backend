package com.giuseppe_tesse.turista.dao;

import java.util.List;
import java.util.Optional;//Optional per i metodi di ricerca che potrebbero non trovare nulla

import com.giuseppe_tesse.turista.model.Utente;

public interface UtenteDAO {

// ==================== CREATE ====================

    Utente create(Utente utente);

// ==================== READ ====================

    List<Utente> findAll();

    Optional<Utente> findById(Long id);

    Optional<Utente> findByEmail(String email);
    
// ==================== UPDATE ====================

    Optional<Utente> update(Utente utente);

// ==================== DELETE ====================

    int deleteAll();

    boolean deleteById(Long id);

    boolean deleteByEmail(String email);
    
}

