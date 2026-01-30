package com.giuseppe_tesse.turista.router;

import java.util.List;

import com.giuseppe_tesse.turista.controller.BookingController;
import com.giuseppe_tesse.turista.controller.Controller;
import com.giuseppe_tesse.turista.controller.FeedbackController;
import com.giuseppe_tesse.turista.controller.ResidenceController;
import com.giuseppe_tesse.turista.controller.HostController;
import com.giuseppe_tesse.turista.controller.UserController;
import com.giuseppe_tesse.turista.dao.impl.BookingDAOImpl;
import com.giuseppe_tesse.turista.dao.impl.FeedbackDAOImpl;
import com.giuseppe_tesse.turista.dao.impl.HostDAOImpl;
import com.giuseppe_tesse.turista.dao.impl.ResidenceDAOImpl;
import com.giuseppe_tesse.turista.dao.impl.UserDAOImpl;
import com.giuseppe_tesse.turista.service.BookingService;
import com.giuseppe_tesse.turista.service.FeedbackService;
import com.giuseppe_tesse.turista.service.HostService;
import com.giuseppe_tesse.turista.service.ResidenceService;
import com.giuseppe_tesse.turista.service.UserService;

import io.javalin.Javalin;

public class Router {

    public static void registerAll(Javalin app) {

        // 1. Inizializzazione DAO
        UserDAOImpl userDAO = new UserDAOImpl();
        ResidenceDAOImpl residenceDAO = new ResidenceDAOImpl();
        BookingDAOImpl bookingDAO = new BookingDAOImpl();
        FeedbackDAOImpl feedbackDAO = new FeedbackDAOImpl();
        HostDAOImpl hostDAO = new HostDAOImpl();

        // 2. Inizializzazione Service (Dependency Injection)
        UserService userService = new UserService(userDAO);
        ResidenceService residenceService = new ResidenceService(residenceDAO);
        BookingService bookingService = new BookingService(bookingDAO);
        FeedbackService feedbackService = new FeedbackService(feedbackDAO);
        HostService hostService = new HostService(hostDAO, userDAO);

        // 3. Registrazione dei Controller nella lista
        List<Controller> controllers = List.of(
            new UserController(userService),
            new ResidenceController(residenceService,hostService),
            new BookingController(bookingService,residenceService,userService),
            new FeedbackController(feedbackService,bookingService,userService),
            new HostController(hostService)
        );

        // 4. Registrazione automatica di tutte le rotte
        controllers.forEach(c -> c.registerRoutes(app));
    }
}