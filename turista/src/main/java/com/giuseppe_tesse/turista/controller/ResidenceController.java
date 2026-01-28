package com.giuseppe_tesse.turista.controller;

import com.giuseppe_tesse.turista.exception.DuplicateResidenceException;
import com.giuseppe_tesse.turista.model.Residence;
import com.giuseppe_tesse.turista.service.ResidenceService;
import com.giuseppe_tesse.turista.exception.ResidenceNotFoundException;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ResidenceController implements Controller {

    private final ResidenceService residenceService;

    public ResidenceController(ResidenceService residenceService) {
        this.residenceService = residenceService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/residences", this::createResidence);
        app.get("/api/v1/residences/{id}", this::getResidenceById);
        app.get("/api/v1/residences", this::getAllResidences);
        app.get("/api/v1/residences/address/{address}/floor/{floor}", this::getResidenceByAddressAndFloor);
        app.get("/api/v1/residences/owner/{ownerId}", this::getResidencesByOwner);
        app.put("/api/v1/residences/{id}", this::updateResidence);
        app.delete("/api/v1/residences/{id}", this::deleteResidenceById);
        app.delete("/api/v1/residences/owner/{ownerId}", this::deleteResidencesByOwner);
        app.delete("/api/v1/residences", this::deleteAllResidences);
    }

    // ==================== CREATE ====================
    private void createResidence(Context ctx) {
        log.info("POST /api/v1/residences - Request to create residence");
        Residence residence = ctx.bodyAsClass(Residence.class);

        try {
            // Allineato ai nomi dei campi del modello Residence (snake_case in Java produce questi getter)
            Residence createdResidence = residenceService.createResidence(
                    residence.getName(),
                    residence.getAddress(),
                    residence.getPrice_per_night(),
                    residence.getNumber_of_rooms(),
                    residence.getGuest_capacity(),
                    residence.getFloor(),
                    residence.getAvailable_from(),
                    residence.getAvailable_to(),
                    residence.getHost()
            );
            ctx.status(HttpStatus.CREATED).json(createdResidence);
            log.info("Residence successfully created: {}", createdResidence);
        } catch (DuplicateResidenceException e) {
            log.error("Failed to create residence: {}", e.getMessage());
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        }
    }

    // ==================== READ ====================
    private void getResidenceById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("GET /api/v1/residences/{} - Request to fetch residence by ID", id);

        try {
            Residence residence = residenceService.getResidenceById(id);
            ctx.status(HttpStatus.OK).json(residence);
            log.info("Residence retrieved successfully: {}", residence);
        } catch (ResidenceNotFoundException e) {
            log.error("Residence not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getAllResidences(Context ctx) {
        log.info("GET /api/v1/residences - Request to fetch all residences");
        try {
            List<Residence> residences = residenceService.getAllResidences();
            ctx.status(HttpStatus.OK).json(residences);
            log.info("All residences retrieved successfully, count: {}", residences.size());
        } catch (Exception e) {
            log.error("Error fetching residences: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    private void getResidenceByAddressAndFloor(Context ctx) {
        String address = ctx.pathParam("address");
        int floor = Integer.parseInt(ctx.pathParam("floor"));
        log.info("GET /api/v1/residences/address/{}/floor/{} - Request to fetch residence by address and floor", address, floor);

        try {
            Residence residence = residenceService.getResidenceByAddressAndFloor(address, floor);
            ctx.status(HttpStatus.OK).json(residence);
            log.info("Residence retrieved successfully: {}", residence);
        } catch (ResidenceNotFoundException e) {
            log.error("Residence not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getResidencesByOwner(Context ctx) {
        Long ownerId = Long.valueOf(ctx.pathParam("ownerId"));
        log.info("GET /api/v1/residences/owner/{} - Request to fetch residences by owner ID", ownerId);

        try {
            List<Residence> residences = residenceService.getResidencesByOwner(ownerId);
            ctx.status(HttpStatus.OK).json(residences);
            log.info("Residences retrieved successfully for owner ID {}: {}", ownerId, residences);
        } catch (ResidenceNotFoundException e) {
            log.error("Residences not found for owner ID {}: {}", ownerId, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    // ==================== UPDATE ====================
    private void updateResidence(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("PUT /api/v1/residences/{} - Request to update residence", id);
        Residence residenceUpdates = ctx.bodyAsClass(Residence.class);
        
        // Assicuriamoci che l'ID del path sia impostato nell'oggetto
        residenceUpdates.setId(id);

        try {
            Residence updatedResidence = residenceService.updateResidence(residenceUpdates);
            ctx.status(HttpStatus.OK).json(updatedResidence);
            log.info("Residence updated successfully: {}", updatedResidence);
        } catch (ResidenceNotFoundException e) {
            log.warn("Residence not found with ID {}: {}", id, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    // ==================== DELETE ====================
    private void deleteResidenceById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("DELETE /api/v1/residences/{} - Request to delete residence", id);

        try {
            residenceService.deleteResidenceById(id);
            ctx.status(HttpStatus.NO_CONTENT);
            log.info("Residence deleted successfully with ID: {}", id);
        } catch (ResidenceNotFoundException e) {
            log.error("Error deleting residence: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void deleteResidencesByOwner(Context ctx) {
        Long ownerId = Long.valueOf(ctx.pathParam("ownerId"));
        log.info("DELETE /api/v1/residences/owner/{} - Request to delete residences by owner ID", ownerId);

        try {
            residenceService.deleteResidencesByOwner(ownerId);
            ctx.status(HttpStatus.NO_CONTENT);
            log.info("Residences deleted successfully for owner ID: {}", ownerId);
        } catch (ResidenceNotFoundException e) {
            log.error("Error deleting residences for owner ID {}: {}", ownerId, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void deleteAllResidences(Context ctx) {
        log.info("DELETE /api/v1/residences - Request to delete all residences");

        try {
            residenceService.deleteAllResidences();
            ctx.status(HttpStatus.NO_CONTENT);
            log.info("All residences deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting all residences: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }
}