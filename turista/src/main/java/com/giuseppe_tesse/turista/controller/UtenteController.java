package com.giuseppe_tesse.turista.controller;
import com.giuseppe_tesse.turista.exception.DuplicateUserException;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import com.giuseppe_tesse.turista.model.Utente;
import com.giuseppe_tesse.turista.service.UtenteService;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UtenteController implements Controller {

    private final UtenteService utenteService;

    public UtenteController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/users", this::createUtente);
        app.get("/api/v1/users/:id", this::getUtenteById);
        app.get("/api/v1/users", this::getAllUsers);
        app.get("/api/v1/users/email/:email", this::getUtenteByEmail);
        app.put("/api/v1/users/:id", this::updateUtente);
        app.delete("/api/v1/users/:id", this::deleteUtente);
        app.delete("/api/v1/users", this::deleteAllUtenti);
        app.delete("/api/v1/users/email/:email", this::deleteUtenteByEmail);
    }

// ==================== CREATE ====================

    private void createUtente(Context ctx){
        log.info("POST /api/v1/users - Richiesta creazione utente");
        Utente utente = ctx.bodyAsClass(Utente.class);

        try{
            Utente createdUtente = utenteService.insertUtente(
                    utente.getNome(),
                    utente.getCognome(),
                    utente.getEmail(),
                    utente.getPassword(),
                    utente.getIndirizzo()
            );
            ctx.status(HttpStatus.CREATED).json(createdUtente);
            log.info("Utente creato con successo: {}", createdUtente);
        } catch (DuplicateUserException e){
            log.error("Errore nella creazione dell'utente: {}", e.getMessage());
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        }
    }



    private void getUtenteById(Context ctx){
        log.info("GET /api/v1/users/{} - Richiesta recupero utente per ID", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        try{
        Utente utente = utenteService.getUtenteById(id);
            ctx.status(HttpStatus.OK).json(utente);
            log.info("Utente recuperato con successo: {}", utente);
        } catch (UserNotFoundException e)  {
            ctx.status(HttpStatus.NOT_FOUND).result("Utente non trovato con ID: " + id+"."+ e.getMessage());
            log.warn("Utente non trovato con ID: {}", id);
        }
    }

    private void getAllUsers(Context ctx){
        log.info("GET /api/v1/users - Richiesta recupero tutti gli utenti");
        ctx.status(HttpStatus.OK).json(utenteService.getAllUtenti());
        log.info("Tutti gli utenti recuperati con successo");
    }

    public void getUtenteByEmail(Context ctx){
        log.info("GET /api/v1/users/email/{} - Richiesta recupero utente per email", ctx.pathParam("email"));
        String email = ctx.pathParam("email");
        try{
            Utente utente = utenteService.getUtenteByEmail(email);
            ctx.status(HttpStatus.OK).json(utente);
            log.info("Utente recuperato con successo: {}", utente);
        } catch (UserNotFoundException e)  {
            ctx.status(HttpStatus.NOT_FOUND).result("Utente non trovato con email: " + email+"."+ e.getMessage());
            log.warn("Utente non trovato con email: {}", email);
        }
    }

// ==================== UPDATE ====================

    private void updateUtente(Context ctx){
        log.info("PUT /api/v1/users/{} - Richiesta aggiornamento utente", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        Utente utenteUpdates = ctx.bodyAsClass(Utente.class);
        try{
            Utente updatedUtente = utenteService.updateUtente(
                utenteUpdates
            );
            ctx.status(HttpStatus.OK).json(updatedUtente);
            log.info("Utente aggiornato con successo: {}", updatedUtente);
        } catch (UserNotFoundException e){
            ctx.status(HttpStatus.NOT_FOUND).result("Utente non trovato con ID: " + id+"."+ e.getMessage());
            log.warn("Utente non trovato con ID: {}", id);
        }
    }

// ==================== DELETE ====================

    private void deleteUtente(Context ctx){
        log.info("DELETE /api/v1/users/{} - Richiesta eliminazione utente", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        try{
            utenteService.deleteUtenteById(id);
            ctx.status(HttpStatus.NO_CONTENT);
            log.info("Utente eliminato con successo con ID: {}", id);
        } catch (UserNotFoundException e){
            ctx.status(HttpStatus.NOT_FOUND).result("Utente non trovato con ID: " + id+"."+ e.getMessage());
            log.warn("Utente non trovato con ID: {}", id);
        }
    }

    private void deleteAllUtenti(Context ctx){
        log.info("DELETE /api/v1/users - Richiesta eliminazione tutti gli utenti");
        utenteService.deleteAllUtenti();
        ctx.status(HttpStatus.NO_CONTENT);
        log.info("Tutti gli utenti eliminati con successo");
    }

    private void deleteUtenteByEmail(Context ctx){
        log.info("DELETE /api/v1/users/email/{} - Richiesta eliminazione utente per email", ctx.pathParam("email"));
        String email = ctx.pathParam("email");
        try{
            utenteService.deleteUtenteByEmail(email);
            ctx.status(HttpStatus.NO_CONTENT);
            log.info("Utente eliminato con successo con email: {}", email);
        } catch (UserNotFoundException e){
            ctx.status(HttpStatus.NOT_FOUND).result("Utente non trovato con email: " + email+"."+ e.getMessage());
            log.warn("Utente non trovato con email: {}", email);
        }
    }

    
}
