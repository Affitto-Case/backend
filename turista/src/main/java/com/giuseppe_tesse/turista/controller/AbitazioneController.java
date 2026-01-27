package com.giuseppe_tesse.turista.controller;
import com.giuseppe_tesse.turista.exception.DuplicateAbitazioneException;
import com.giuseppe_tesse.turista.model.Abitazione;
import com.giuseppe_tesse.turista.service.AbitazioneService;

import java.util.List;

import com.giuseppe_tesse.turista.exception.AbitazioneNotFoundException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbitazioneController implements Controller {

    private final AbitazioneService abitazioneService;

    public AbitazioneController(AbitazioneService abitazioneService) {
        this.abitazioneService = abitazioneService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/abitazioni", this::createAbitazione);
        app.get("/api/v1/abitazioni/:id", this::getAbitazioneById);
        app.get("/api/v1/abitazioni", this::getAllAbitazioni);
        app.get("/api/v1/abitazioni/indirizzo/:indirizzo/piano/:piano", this::getAbitazioneByIndirizzoAndPiano);
        app.get("/api/v1/abitazioni/proprietario/:proprietario_id", this::getAbitazioneByProprietario);
        app.put("/api/v1/abitazioni/:id", this::updateAbitazione);
        app.delete("/api/v1/abitazioni/:id", this::deleteAbitazioneById);
        app.delete("/api/v1/abitazioni/proprietario/:proprietario_id", this::deleteAbitazioneByProprietario);
        app.delete("/api/v1/abitazioni", this::deleteAllAbitazioni);
    }

// ==================== CREATE ====================

    private void createAbitazione(Context ctx){
        log.info("POST /api/v1/abitazioni - Richiesta creazione abitazione");
        Abitazione abitazione = ctx.bodyAsClass(Abitazione.class);
        try{
            Abitazione createdAbitazione = abitazioneService.insertAbitazione(
                abitazione.getNome(),     
                abitazione.getIndirizzo(),
                abitazione.getPrezzo_per_notte(),
                abitazione.getNumero_locali(),
                abitazione.getCapacita_ospiti(),
                abitazione.getPiano(),
                abitazione.getData_disponibilita_inizio(),
                abitazione.getData_disponibilita_fine(),
                abitazione.getHost()
            );
            ctx.status(HttpStatus.CREATED).json(createdAbitazione);
            log.info("Abitazione creata con successo: {}", createdAbitazione);
        } catch (DuplicateAbitazioneException e){
            log.error("Errore nella creazione dell'abitazione: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

// ==================== READ ====================

    private void getAbitazioneById(Context ctx){
        log.info("GET /api/v1/abitazioni/{} - Richiesta recupero abitazione per ID", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        try{
            Abitazione abitazione = abitazioneService.getAbitazioneById(id);
            ctx.status(HttpStatus.OK).json(abitazione);
            log.info("Abitazione recuperata con successo: {}", abitazione);
        } catch (AbitazioneNotFoundException e){
            log.error("Errore nel recupero dell'abitazione: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getAllAbitazioni(Context ctx){
        log.info("GET /api/v1/abitazioni - Richiesta recupero di tutte le abitazioni");
        try{
            List<Abitazione> abitazioni = abitazioneService.getAllAbitazioni();
            ctx.status(HttpStatus.OK).json(abitazioni);
            log.info("Abitazioni recuperate con successo: {}", abitazioni);
        } catch (Exception e){
            log.error("Errore nel recupero delle abitazioni: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    private void getAbitazioneByIndirizzoAndPiano(Context ctx){
        String indirizzo = ctx.pathParam("indirizzo");
        int piano = Integer.parseInt(ctx.pathParam("piano"));
        log.info("GET /api/v1/abitazioni/indirizzo/{}/piano/{} - Richiesta recupero abitazione per Indirizzo e Piano", indirizzo, piano);
        try{
            Abitazione abitazione = abitazioneService.getAbitazioneByIndirizzoAndPiano(indirizzo, piano);
            ctx.status(HttpStatus.OK).json(abitazione);
            log.info("Abitazione recuperata con successo: {}", abitazione);
        } catch (AbitazioneNotFoundException e){
            log.error("Errore nel recupero dell'abitazione: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getAbitazioneByProprietario(Context ctx){
        Long proprietario_id = Long.valueOf(ctx.pathParam("proprietario_id"));
        log.info("GET /api/v1/abitazioni/proprietario/{} - Richiesta recupero abitazione per ID proprietario", proprietario_id);
        try{
            List<Abitazione> abitazione = abitazioneService.getAbitazioneByProprietario(proprietario_id);
            ctx.status(HttpStatus.OK).json(abitazione);
            log.info("Abitazione recuperata con successo: {}", abitazione);
        } catch (AbitazioneNotFoundException e){
            log.error("Errore nel recupero dell'abitazione: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

// ==================== UPDATE ====================


    private void updateAbitazione(Context ctx){
        log.info("PUT /api/v1/abitazioni/{} - Richiesta aggiornamento abitazione", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        Abitazione abitazioneUpdates = ctx.bodyAsClass(Abitazione.class);
        try{
            Abitazione updatedAbitazione = abitazioneService.updateAbitazione(
                abitazioneUpdates
            );
            ctx.status(HttpStatus.OK).json(updatedAbitazione);
            log.info("Abitazione aggiornata con successo: {}", updatedAbitazione);
        } catch (AbitazioneNotFoundException e){
            ctx.status(HttpStatus.NOT_FOUND).result("Abitazione non trovata con ID: " + id+"."+ e.getMessage());
            log.warn("Abitazione non trovata con ID: {}", id);
        }
    }

// ==================== DELETE ====================

    private void deleteAbitazioneById(Context ctx){
        log.info("DELETE /api/v1/abitazioni/{} - Richiesta eliminazione abitazione", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        try{
            boolean deleted = abitazioneService.deleteAbitazioneById(id);
            if(deleted){
                ctx.status(HttpStatus.OK).result("Abitazione eliminata con successo con ID: " + id);
                log.info("Abitazione eliminata con successo con ID: {}", id);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Abitazione non trovata con ID: " + id);
                log.warn("Abitazione non trovata con ID: {}", id);
            }
        } catch (AbitazioneNotFoundException e){
            log.error("Errore nell'eliminazione dell'abitazione: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void deleteAbitazioneByProprietario(Context ctx){
        log.info("DELETE /api/v1/abitazioni/proprietario/{} - Richiesta eliminazione abitazione per ID proprietario", ctx.pathParam("proprietario_id"));
        Long proprietario_id = Long.valueOf(ctx.pathParam("proprietario_id"));
        try{
            boolean deleted = abitazioneService.deleteAbitazioneByProprietario(proprietario_id);
            if(deleted){
                ctx.status(HttpStatus.OK).result("Abitazione eliminata con successo per ID proprietario: " + proprietario_id);
                log.info("Abitazione eliminata con successo per ID proprietario: {}", proprietario_id);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Abitazione non trovata per ID proprietario: " + proprietario_id);
                log.warn("Abitazione non trovata per ID proprietario: {}", proprietario_id);
            }
        } catch (AbitazioneNotFoundException e){
            log.error("Errore nell'eliminazione dell'abitazione: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void deleteAllAbitazioni(Context ctx){
        log.info("DELETE /api/v1/abitazioni - Richiesta eliminazione di tutte le abitazioni");
        try{
            int deletedCount = abitazioneService.deleteAllAbitazioni();
            ctx.status(HttpStatus.OK).result("Totale abitazioni eliminate: " + deletedCount);
            log.info("Tutte le abitazioni eliminate con successo, totale: {}", deletedCount);
        } catch (Exception e){
            log.error("Errore nell'eliminazione delle abitazioni: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }
    
}
