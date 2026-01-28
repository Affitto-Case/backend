package com.giuseppe_tesse.turista.controller;

import com.giuseppe_tesse.turista.service.HostService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

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
        ctx.status(HttpStatus.CREATED).json(hostService.createHost(userId));
    }

    private void getAllHosts(Context ctx) {
        ctx.json(hostService.getAllHosts());
    }

    private void getHostById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        ctx.json(hostService.getHostById(id));
    }
}