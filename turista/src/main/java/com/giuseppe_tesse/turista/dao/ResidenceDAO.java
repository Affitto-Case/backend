package com.giuseppe_tesse.turista.dao;

import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.dto.MostPopularResidenceDTO;
import com.giuseppe_tesse.turista.model.Residence;

public interface ResidenceDAO {

// ==================== CREATE ====================

    Residence create(Residence residence);

// ==================== READ ====================

    List<Residence> findAll();

    // Ricerca le residenze appartenenti a un determinato host (proprietario)
    List<Residence> findByHostId(Long hostId);

    Optional<Residence> findById(Long id);
    
    // Ricerca per indirizzo e piano
    Optional<Residence> findByAddressAndFloor(String address, int floor);

    //Ricerca per codice host
    List<Residence> findByHostCode(String code);

    Optional<MostPopularResidenceDTO> getMostPopularResidenceLastMonth();

// ==================== UPDATE ====================

    Optional<Residence> update(Residence residence);

// ==================== DELETE ====================

    int deleteAll();

    boolean deleteById(Long id);

    // Elimina tutte le residenze di un determinato host
    boolean deleteByHostId(Long hostId);
    
}