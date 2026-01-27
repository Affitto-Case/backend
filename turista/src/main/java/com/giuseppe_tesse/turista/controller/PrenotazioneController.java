package com.giuseppe_tesse.turista.controller;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import com.giuseppe_tesse.turista.exception.DuplicatePrenotazioneException;
import com.giuseppe_tesse.turista.exception.PrenotazioneNotFoundException;
import com.giuseppe_tesse.turista.model.Prenotazione;
import com.giuseppe_tesse.turista.service.PrenotazioneService;

@Slf4j
public class PrenotazioneController implements Controller {

    private final PrenotazioneService prenotazioneService;

    public PrenotazioneController(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/prenotazioni", this::insertPrenotazione);
        app.get("/api/v1/prenotazioni", this::getAllPrenotazioni);
        app.get("/api/v1/prenotazioni/:id", this::getPrenotazioneById);
        app.get("/api/v1/prenotazioni/abitazione/:abitazioneId", this::getPrenotazioneByAbitazioneId);
        app.put("/api/v1/prenotazioni/:id", this::updatePrenotazione);
        app.delete("/api/v1/prenotazioni/:id", this::deletePrenotazioneById);
        app.delete("/api/v1/prenotazioni", this::deleteAllPrenotazioni);
    }

// ==================== CREATE ====================

    private void insertPrenotazione(Context ctx){
        log.info("POST /api/v1/prenotazioni - Richiesta creazione prenotazione");
        Prenotazione prenotazione = ctx.bodyAsClass(Prenotazione.class);

        try{
            Prenotazione createdPrenotazione = prenotazioneService.insertPrenotazione(
                    prenotazione.getAbitazione(),
                    prenotazione.getUtente(),
                    prenotazione.getData_inizio(),
                    prenotazione.getData_fine()
            );
            ctx.status(HttpStatus.CREATED).json(createdPrenotazione);
            log.info("Prenotazione creata con successo: {}", createdPrenotazione);
        } catch (DuplicatePrenotazioneException e){
            log.error("Errore nella creazione della prenotazione: {}", e.getMessage());
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        }

    }

// ==================== READ ====================

    private void getAllPrenotazioni(Context ctx){
        log.info("GET api/v1/prenotazioni - Richiesta recupero di tutte le prenotazioni");
        ctx.status(HttpStatus.OK).json(prenotazioneService.getAllPrenotazioni());
        log.info("Recupero di tutte le prenotazioni avvenuto con successo");
    }

    private void getPrenotazioneById(Context ctx){
        log.info("GET /api/v1/prenotazioni/{} - Richiesta recupero prenotazione per ID", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        try{
            Prenotazione prenotazione = prenotazioneService.getPrenotazioneById(id);
            ctx.status(HttpStatus.OK).json(prenotazione);
            log.info("Recupero della prenotazione avvenuto con successo: {}", prenotazione);
        } catch (PrenotazioneNotFoundException e){
            log.error("Errore nel recupero della prenotazione: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getPrenotazioneByAbitazioneId(Context ctx){
        log.info("GET /api/v1/prenotazioni/abitazione/{} - Richiesta recupero prenotazioni per Abitazione ID", ctx.pathParam("abitazioneId"));
        Long abitazioneId = Long.valueOf(ctx.pathParam("abitazioneId"));
        try{
            ctx.status(HttpStatus.OK).json(prenotazioneService.getPrenotazioniByAbitazioneId(abitazioneId));
            log.info("Recupero delle prenotazioni per l'abitazione avvenuto con successo");
        } catch (PrenotazioneNotFoundException e){
            log.error("Errore nel recupero delle prenotazioni per l'abitazione: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }


// ==================== UPDATE ====================

    private void updatePrenotazione(Context ctx){
        log.info("PUT /api/v1/prenotazioni/{} - Richiesta aggiornamento prenotazione", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        Prenotazione prenotazioneUpdates = ctx.bodyAsClass(Prenotazione.class);
        try{
            Prenotazione existingPrenotazione = prenotazioneService.getPrenotazioneById(id);

            existingPrenotazione.setData_inizio(prenotazioneUpdates.getData_inizio());
            existingPrenotazione.setData_fine(prenotazioneUpdates.getData_fine());
            Prenotazione updatedPrenotazione = prenotazioneService.updatePrenotazione(existingPrenotazione);
            ctx.status(HttpStatus.OK).json(updatedPrenotazione);
            log.info("Aggiornamento della prenotazione avvenuto con successo: {}", updatedPrenotazione);
        } catch (PrenotazioneNotFoundException e){
            log.error("Errore nell'aggiornamento della prenotazione: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

// ==================== DELETE ====================

    private void deletePrenotazioneById(Context ctx){
        log.info("DELETE /api/v1/prenotazioni/{} - Richiesta eliminazione prenotazione", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        try{
            boolean deleted = prenotazioneService.deletePrenotazioneById(id);
            if(deleted){
                ctx.status(HttpStatus.NO_CONTENT);
                log.info("Eliminazione della prenotazione avvenuta con successo per ID: {}", id);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Prenotazione non trovata con ID: " + id);
                log.warn("Prenotazione non trovata con ID: {}", id);
            }
        } catch (PrenotazioneNotFoundException e){
            log.error("Errore nell'eliminazione della prenotazione: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void deleteAllPrenotazioni(Context ctx){
        log.info("DELETE /api/v1/prenotazioni - Richiesta eliminazione tutte le prenotazioni");
        prenotazioneService.deleteAllPrenotazioni();
        ctx.status(HttpStatus.NO_CONTENT);
        log.info("Eliminazione di tutte le prenotazioni avvenuta con successo");
    }
    
}
