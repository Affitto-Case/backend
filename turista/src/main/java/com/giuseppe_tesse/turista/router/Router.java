package com.giuseppe_tesse.turista.router;

import java.util.List;

import com.giuseppe_tesse.turista.controller.AbitazioneController;
import com.giuseppe_tesse.turista.controller.Controller;
import com.giuseppe_tesse.turista.controller.FeedbackController;
import com.giuseppe_tesse.turista.controller.PrenotazioneController;
import com.giuseppe_tesse.turista.controller.UtenteController;
import com.giuseppe_tesse.turista.dao.UtenteDAO;
import com.giuseppe_tesse.turista.dao.impl.UtenteDAOImpl;
import com.giuseppe_tesse.turista.service.UtenteService;

import io.javalin.Javalin;

public class Router {

    public static void registerAll(Javalin app) {

        UtenteDAO utenteDAO = new UtenteDAOImpl();
        UtenteService utenteService = new UtenteService(utenteDAO);

        List<Controller> controllers = List.of(
            new UtenteController(utenteService)
            // new AbitazioneController(),
            // new PrenotazioneController(),
            // new FeedbackController()
        );

        controllers.forEach(c -> c.registerRoutes(app));
    }
    
}
