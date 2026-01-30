package com.giuseppe_tesse.turista.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.giuseppe_tesse.turista.dto.mapper.HostMapper;
import com.giuseppe_tesse.turista.dto.response.HostResponseDTO;
import com.giuseppe_tesse.turista.exception.DuplicateHostException;
import com.giuseppe_tesse.turista.exception.HostNotFoundException;
import com.giuseppe_tesse.turista.model.Host;
import com.giuseppe_tesse.turista.service.HostService;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HostController implements Controller {
    private final HostService hostService;

    public HostController(HostService hostService) {
        this.hostService = hostService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/hosts/{userId}", this::createHost);
        app.get("/api/v1/hosts", this::getAllHosts);
        app.get("/api/v1/hosts/{id}", this::getHostById);
    }

    private void createHost(Context ctx) {
        Long userId = Long.valueOf(ctx.pathParam("userId"));
        try{
            Host host = hostService.createHost(userId);
            HostResponseDTO responseDTO = HostMapper.toResponseDTO(host);
            ctx.status(HttpStatus.CREATED).json(responseDTO);
            log.info("Host successfully created");
        }catch(DuplicateHostException e){
            log.error("Failed to create host: {}", e.getMessage());
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        }
    }

    private void getAllHosts(Context ctx) {
        try {
            List<Host> hosts = hostService.getAllHosts();
            List<HostResponseDTO> responseDTOs;
            responseDTOs = hosts.stream()
                    .map(HostMapper::toResponseDTO)
                    .collect(Collectors.toList());
            ctx.status(HttpStatus.OK).json(responseDTOs);
        } catch (Exception e) {
            log.error("Error fetching host: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    private void getHostById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        try{
            Host host = hostService.getHostById(id);
            HostResponseDTO responseDTO = HostMapper.toResponseDTO(host);
            ctx.status(HttpStatus.OK).json(responseDTO);
        }catch(HostNotFoundException e){
            log.error("Host not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage()); 
        }
    }
}