package com.giuseppe_tesse.turista.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.giuseppe_tesse.turista.dto.MostPopularResidenceDTO;
import com.giuseppe_tesse.turista.dto.mapper.ResidenceMapper;
import com.giuseppe_tesse.turista.dto.request.ResidenceRequestDTO;
import com.giuseppe_tesse.turista.dto.response.ResidenceResponseDTO;
import com.giuseppe_tesse.turista.exception.DuplicateResidenceException;
import com.giuseppe_tesse.turista.exception.HostNotFoundException;
import com.giuseppe_tesse.turista.exception.ResidenceNotFoundException;
import com.giuseppe_tesse.turista.model.Host;
import com.giuseppe_tesse.turista.model.Residence;
import com.giuseppe_tesse.turista.service.HostService;
import com.giuseppe_tesse.turista.service.ResidenceService;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResidenceController implements Controller {

    private final ResidenceService residenceService;
    private final HostService hostService;

    public ResidenceController(ResidenceService residenceService,HostService hostService) {
        this.residenceService = residenceService;
        this.hostService = hostService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/residences/{hostId}", this::createResidence);
        app.get("/api/v1/residences/{id}", this::getResidenceById);
        app.get("/api/v1/residences", this::getAllResidences);
        app.get("/api/v1/residences/address/{address}/floor/{floor}", this::getResidenceByAddressAndFloor);
        app.get("/api/v1/residences/owner/{ownerId}", this::getResidencesByOwner);
        app.get("/api/v1/residences/owner/host_code/{hostCode}", this::getResidencesByHostCode);
        app.get("/api/v1/residences/stats/mprlm", this::get_MPRLM);
        app.put("/api/v1/residences/{id}", this::updateResidence);
        app.delete("/api/v1/residences/{id}", this::deleteResidenceById);
        app.delete("/api/v1/residences/owner/{ownerId}", this::deleteResidencesByOwner);
        app.delete("/api/v1/residences", this::deleteAllResidences);
    }

    // ==================== CREATE ====================
    private void createResidence(Context ctx) {
        log.info("POST /api/v1/residences - Request to create residence");
        try {
            ResidenceRequestDTO requestDTO = ctx.bodyAsClass(ResidenceRequestDTO.class);
            Host host = hostService.getHostById(requestDTO.getHostId());
            Residence residence = ResidenceMapper.toEntity(requestDTO,host);
            Residence createdResidence = residenceService.createResidence(residence);
            ResidenceResponseDTO responseDTO= ResidenceMapper.toResponseDTO(createdResidence);
            ctx.status(HttpStatus.CREATED).json(responseDTO);
            log.info("Residence successfully created: {}", createdResidence.getId());
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
            ResidenceResponseDTO responseDTO = ResidenceMapper.toResponseDTO(residence);
            ctx.status(HttpStatus.OK).json(responseDTO);
            log.info("Residence retrieved successfully: {}", id);
        } catch (ResidenceNotFoundException e) {
            log.error("Residence not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getAllResidences(Context ctx) {
        log.info("GET /api/v1/residences - Request to fetch all residences");
        try {
            List<Residence> residences = residenceService.getAllResidences();
            List<ResidenceResponseDTO> responseDTOs = residences.stream()
                    .map(ResidenceMapper::toResponseDTO)
                    .collect(Collectors.toList());
             ctx.status(HttpStatus.OK).json(responseDTOs);
            log.info("All residences retrieved successfully, count: {}", responseDTOs.size());
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
            ResidenceResponseDTO responseDTO = ResidenceMapper.toResponseDTO(residence);
            ctx.status(HttpStatus.OK).json(responseDTO);
            log.info("Residence retrieved successfully: {}", residence.getId());
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
            List<ResidenceResponseDTO> responseDTOs = residences.stream()
                    .map(ResidenceMapper::toResponseDTO)
                    .collect(Collectors.toList());
            ctx.status(HttpStatus.OK).json(responseDTOs);
            log.info("Residences retrieved successfully for owner ID {}: {}", ownerId, responseDTOs);
        } catch (ResidenceNotFoundException e) {
            log.error("Residences not found for owner ID {}: {}", ownerId, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        } catch (HostNotFoundException e){
            log.error("Host not found with ID {}: {}", ownerId, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getResidencesByHostCode(Context ctx){
        String host_code = ctx.pathParam("hostCode");
        log.info("/api/v1/residences/owner/{} - Request to fetch residences by host code", host_code);

        try{
            List<Residence> residences = residenceService.getResidenceByHostCode(host_code);
            List<ResidenceResponseDTO> responseDTOs = residences.stream()
                    .map(ResidenceMapper::toResponseDTO)
                    .collect(Collectors.toList());
            ctx.status(HttpStatus.OK).json(responseDTOs);
            log.info("Residences retrieved successfully for host code {}: {}", host_code, responseDTOs);
        } catch (ResidenceNotFoundException e) {
            log.error("Residences not found for host code {}: {}", host_code, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void get_MPRLM(Context ctx) {
        log.info("GET /api/v1/residences/stats/mprlm");
        try{
            MostPopularResidenceDTO dto = residenceService.get_MPRLM();
            ctx.status(200).json(dto);
            log.info("Most popular residence retrieved");
        } catch (ResidenceNotFoundException e) {
            log.error("Residences not found : {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    // ==================== UPDATE ====================
    private void updateResidence(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("PUT /api/v1/residences/{} - Request to update residence", id);
        Residence residenceUpdates = ctx.bodyAsClass(Residence.class);
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