package com.giuseppe_tesse.turista.service;

import java.time.LocalDate;
import java.util.List;

import com.giuseppe_tesse.turista.dao.ResidenceDAO;
import com.giuseppe_tesse.turista.exception.DuplicateResidenceException;
import com.giuseppe_tesse.turista.exception.ResidenceNotFoundException;
import com.giuseppe_tesse.turista.model.Residence;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResidenceService {

    private final ResidenceDAO residenceDAO;

    public ResidenceService(ResidenceDAO residenceDAO) {
        this.residenceDAO = residenceDAO;
    }

    // ==================== CREATE ====================

    public Residence createResidence(String name, String address, double pricePerNight,
                                     int rooms, int guestCapacity, int floor,
                                     LocalDate availabilityStart, LocalDate availabilityEnd,
                                     Long hostId) {

        log.info("Attempting to insert residence - Name: {}, Address: {}, Floor: {}", name, address, floor);

        Residence residence = new Residence(name, address, pricePerNight,
                                            rooms, guestCapacity, floor,
                                            availabilityStart, availabilityEnd, hostId);

        if (residenceDAO.findByAddressAndFloor(address, floor).isPresent()) {
            log.warn("Failed to insert residence - already exists at Address: {} Floor: {}", address, floor);
            throw new DuplicateResidenceException("address and floor", address + ", " + floor);
        }

        return residenceDAO.create(residence);
    }

    // ==================== READ ====================

    public List<Residence> getAllResidences() {
        log.info("Fetching all residences");
        return residenceDAO.findAll();
    }

    public List<Residence> getResidencesByOwner(Long ownerId) {
        log.info("Fetching residences by Owner ID: {}", ownerId);
        return residenceDAO.findByHostId(ownerId);
    }

    public Residence getResidenceById(Long id) {
        log.info("Fetching residence by ID: {}", id);
        return residenceDAO.findById(id)
                .orElseThrow(() -> {
                    log.warn("Residence not found with ID: {}", id);
                    return new ResidenceNotFoundException(id);
                });
    }

    public Residence getResidenceByAddressAndFloor(String address, int floor) {
        log.info("Fetching residence by Address: {} and Floor: {}", address, floor);
        return residenceDAO.findByAddressAndFloor(address, floor)
                .orElseThrow(() -> {
                    log.warn("Residence not found at Address: {} and Floor: {}", address, floor);
                    return new ResidenceNotFoundException("Residence not found at Address: " + address + " and Floor: " + floor);
                });
    }

    // ==================== UPDATE ====================

    public Residence updateResidence(Residence residence) {
        log.info("Attempting to update residence with ID: {}", residence.getId());
        return residenceDAO.update(residence)
                .orElseThrow(() -> {
                    log.warn("Update failed - Residence not found with ID: {}", residence.getId());
                    return new ResidenceNotFoundException(residence.getId());
                });
    }

    // ==================== DELETE ====================

    public int deleteAllResidences() {
        log.info("Deleting all residences");
        return residenceDAO.deleteAll();
    }

    public boolean deleteResidenceById(Long id) {
        log.info("Attempting to delete residence with ID: {}", id);
        boolean deleted = residenceDAO.deleteById(id);
        if (!deleted) {
            log.warn("Failed to delete - Residence not found with ID: {}", id);
            throw new ResidenceNotFoundException(id);
        }
        return true;
    }

    public boolean deleteResidencesByOwner(Long ownerId) {
        log.info("Attempting to delete residences by Owner ID: {}", ownerId);
        boolean deleted = residenceDAO.deleteByHostId(ownerId);
        if (!deleted) {
            log.warn("Failed to delete - No residences found for Owner ID: {}", ownerId);
            throw new ResidenceNotFoundException("No residences found for Owner ID: " + ownerId);
        }
        return true;
    }
}
