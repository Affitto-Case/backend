package com.giuseppe_tesse.turista.service;

import java.util.List;

import com.giuseppe_tesse.turista.dao.HostDAO;
import com.giuseppe_tesse.turista.dao.ResidenceDAO;
import com.giuseppe_tesse.turista.dto.AVGNumberOfBeds;
import com.giuseppe_tesse.turista.dto.MostPopularResidenceDTO;
import com.giuseppe_tesse.turista.exception.DuplicateResidenceException;
import com.giuseppe_tesse.turista.exception.ResidenceNotFoundException;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import com.giuseppe_tesse.turista.model.Host;
import com.giuseppe_tesse.turista.model.Residence;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResidenceService {

    private final ResidenceDAO residenceDAO;
    private final HostDAO hostDAO;

    public ResidenceService(ResidenceDAO residenceDAO,HostDAO hostDAO) {
        this.residenceDAO = residenceDAO;
        this.hostDAO = hostDAO;
    }

    // ==================== CREATE ====================

    public Residence createResidence(Residence residence) {
    log.info("Attempting to insert residence - Name: {}, Address: {}, Floor: {}", 
            residence.getName(), residence.getAddress(), residence.getFloor());

    if (residenceDAO.findByAddressAndFloor(residence.getAddress(), residence.getFloor()).isPresent()) {
        log.warn("Failed to insert residence - already exists at Address: {} Floor: {}", 
                residence.getAddress(), residence.getFloor());
        throw new DuplicateResidenceException("address and floor", residence.getAddress() + ", " + residence.getFloor());
    }

    if (residence.getAvailable_from().isAfter(residence.getAvailable_to())) {
        throw new IllegalArgumentException("Start date cannot be after end date");
    }

    Residence newResidence = new Residence(
            residence.getName(), residence.getAddress(), residence.getPrice_per_night(),
            residence.getNumber_of_rooms(), residence.getGuest_capacity(), residence.getFloor(),
            residence.getAvailable_from(), residence.getAvailable_to(), residence.getHost()
    );

    return residenceDAO.create(newResidence);
}
    // ==================== READ ====================

    public List<Residence> getAllResidences() {
        log.info("Fetching all residences");
        if(!residenceDAO.findAll().isEmpty()){
            return residenceDAO.findAll();
        }else{
            throw new ResidenceNotFoundException("No residence register");
        }
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

public List<Residence> getResidenceByHostCode(String code) {
    log.info("Fetching residences by host code: {}", code);

    Host host = hostDAO.findByHostCode(code)
        .orElseThrow(() -> new UserNotFoundException(code));

    return residenceDAO.findByHostId(host.getId());
}

    public MostPopularResidenceDTO get_MPRLM(){
        log.info("Fetching the most popular home in the last month");
        return residenceDAO.getMostPopularResidenceLastMonth()
            .orElseThrow(() -> {
                        log.warn("Residence not found");
                        return new ResidenceNotFoundException("Residence not found");
                    });
    }

    public AVGNumberOfBeds getAvgNumberOfBeds(){
        log.info("Calculate average number of beds");
        return residenceDAO.getAvgNumberOfBeds()
                .orElseThrow(() -> {
                        log.warn("Residence not found");
                        return new ResidenceNotFoundException("Residence not found");
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