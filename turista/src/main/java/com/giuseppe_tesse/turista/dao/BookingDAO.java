package com.giuseppe_tesse.turista.dao;

import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.dto.TopHostDTO;
import com.giuseppe_tesse.turista.model.Booking;

public interface BookingDAO {

// ==================== CREATE ====================

    Booking create(Booking booking);

// ==================== READ ====================

    Optional<Booking> findById(Long id);

    // Restituisce una lista di prenotazioni filtrate per l'ID della residenza
    Optional<List<Booking>> findByResidenceId(Long residenceId);

    List<Booking> findAll();

    Optional<Booking> findLastBookingByUserId(Long residenceId);


    List<TopHostDTO> getMostPopularHostsLastMonth();

// ==================== UPDATE ====================

    Optional<Booking> update(Booking booking);
    int countTotalBookingsByHostCode(String hostCode);

// ==================== DELETE ====================

    boolean deleteById(Long id);

    int deleteAll();
}