package com.giuseppe_tesse.turista.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.giuseppe_tesse.turista.dto.TopHostDTO;
import com.giuseppe_tesse.turista.dto.mapper.HostMapper;
import com.giuseppe_tesse.turista.dto.request.CreateHostDTO;
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
        app.post("/api/v1/hosts", this::createHost);
        app.get("/api/v1/hosts", this::getAllHosts);
        app.get("/api/v1/hosts/{id}", this::getHostById);
        app.get("/api/v1/hosts/stats/count", this::getHostCount);
        app.get("/api/v1/super_hosts", this::getAllSuperHosts);
        app.get("/api/v1/stats/hosts/top", this::getTopHosts);
    }

    private void createHost(Context ctx) {
        CreateHostDTO resp = ctx.bodyAsClass(CreateHostDTO.class);
        Long userId = resp.getUserId();
        try {
            Host host = hostService.createHost(userId);
            HostResponseDTO responseDTO = HostMapper.toResponseDTO(host);
            ctx.status(HttpStatus.CREATED).json(responseDTO);
            log.info("Host successfully created");
        } catch (DuplicateHostException e) {
            log.error("Failed to create host: {}", e.getMessage());
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        }
    }

    private void getHostCount(Context ctx) {
        log.info("GET /api/v1/hosts/stats/count");
        ctx.status(HttpStatus.OK).json(hostService.getHostCount());
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

    private void getAllSuperHosts(Context ctx) {
        try {
            List<Host> hosts = hostService.getAllSuperHosts();
            List<HostResponseDTO> responseDTOs;
            responseDTOs = hosts.stream()
                    .map(HostMapper::toResponseDTO)
                    .collect(Collectors.toList());
            ctx.status(HttpStatus.OK).json(responseDTOs);
        } catch (HostNotFoundException e) {
            log.error("Host not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getHostById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        try {
            Host host = hostService.getHostById(id);
            HostResponseDTO responseDTO = HostMapper.toResponseDTO(host);
            ctx.status(HttpStatus.OK).json(responseDTO);
        } catch (HostNotFoundException e) {
            log.error("Host not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    public void getTopHosts(Context ctx) {
        log.info("GET /api/v1/stats/hosts/top");
        try {
            List<TopHostDTO> result = hostService.getTopHostsLastMonth();
            ctx.status(200).json(result);
            log.info("Returned {} top hosts", result.size());
        } catch (HostNotFoundException e) {
            log.error("Host not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }
}