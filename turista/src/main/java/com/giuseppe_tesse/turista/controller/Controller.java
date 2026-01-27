package com.giuseppe_tesse.turista.controller;
import io.javalin.Javalin;

public interface Controller {

    void registerRoutes(Javalin app);
    
}
