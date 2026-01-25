package com.giuseppe_tesse.turista.dao;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.model.Abitazione;

public class AbitazioneDao {

    public interface AbitazioneDAO{

// ==================== CREATE ====================

    Abitazione create(Abitazione abitazione);

// ==================== READ ====================

    List<Abitazione> readAll();

    Optional<Abitazione> readByProprietario(Long proprietario_id);

    Optional<Abitazione> readById(Long id);

// ==================== UPDATE ====================

    void update(Abitazione abitazione);

// ==================== DELETE ====================

    int deleteAll();

    boolean deleteById(Long id);

    boolean deleteByProprietario(Long proprietario_id);

    }
    
}
