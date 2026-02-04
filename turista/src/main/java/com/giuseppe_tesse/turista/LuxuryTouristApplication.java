package com.giuseppe_tesse.turista;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.giuseppe_tesse.turista.router.Router;
import com.giuseppe_tesse.turista.util.DatabaseConnection;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class LuxuryTouristApplication {

    public static void main(String[] args) {

        DatabaseConnection.init("com/giuseppe_tesse/turista/resources/application.properties");
        System.out.println("Properties inizializzate con successo.");

        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 

        
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper, true));
            config.http.defaultContentType = "application/json";
        });

        Router.registerAll(app);
        
        app.options("/*", ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
            ctx.status(200);
        });

        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
        });

        app.start(8080);
        
        System.out.println("Luxury Tourist Application avviata su http://localhost:8080");
    }
}