package com.giuseppe_tesse.turista.controller;
import com.giuseppe_tesse.turista.model.Feedback;
import com.giuseppe_tesse.turista.service.FeedbackService;
import com.giuseppe_tesse.turista.exception.FeedbackNotFoundException;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeedbackController implements Controller{

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/feedbacks", this::insertFeedback);
        app.get("/api/v1/feedbacks/:id", this::getFeedbackById);
        app.get("/api/v1/feedbacks", this::getAllFeedbacks);
        app.get("/api/v1/feedbacks/utente/:utente_id", this::getFeedbacksByUtente);
        app.get("/api/v1/feedbacks/struttura/:prenotazione_id", this::getFeedbacksByStruttura);
        app.get("/api/v1/feedbacks/utente/:utente_id/prenotazione/:prenotazione_id", this::getFeedbackByUtenteAndPrenotazione);
        app.put("/api/v1/feedbacks/:id", this::updateFeedbackComment);
        app.delete("/api/v1/feedbacks", this::deleteAllFeedbacks);
        app.delete("/api/v1/feedbacks/:id", this::deleteFeedback);
    }

// ==================== CREATE ====================

    private void insertFeedback(Context ctx){
        log.info("POST /api/v1/feedbacks - Richiesta creazione feedback");
        Feedback feedback = ctx.bodyAsClass(Feedback.class);

        try{
            Feedback createdFeedback = feedbackService.insertFeedback(
                    feedback.getPrenotazione(),
                    feedback.getTitolo(),
                    feedback.getValutazione(),
                    feedback.getCommento()
            );
            ctx.status(HttpStatus.CREATED).json(createdFeedback);
            log.info("Feedback creato con successo: {}", createdFeedback);
        } catch (Exception e){
            log.error("Errore nella creazione del feedback: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }


// ==================== READ ====================

    private void getFeedbackById(Context ctx){
        log.info("GET /api/v1/feedbacks/{} - Richiesta recupero feedback per ID", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        try{
            Feedback feedback = feedbackService.getFeedbackById(id);
            ctx.status(HttpStatus.OK).json(feedback);
            log.info("Feedback recuperato con successo: {}", feedback);
        } catch (FeedbackNotFoundException e){
            log.error("Errore nel recupero del feedback: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getAllFeedbacks(Context ctx){
        log.info("GET /api/v1/feedbacks - Richiesta recupero di tutti i feedback");
        try{
            var feedbacks = feedbackService.getAllFeedbacks();
            ctx.status(HttpStatus.OK).json(feedbacks);
            log.info("Feedbacks recuperati con successo, totale: {}", feedbacks.size());
        } catch (Exception e){
            log.error("Errore nel recupero dei feedbacks: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    private void getFeedbacksByUtente(Context ctx){
        log.info("GET /api/v1/feedbacks/utente/{} - Richiesta recupero feedback per ID utente", ctx.pathParam("utente_id"));
        Long utente_id = Long.valueOf(ctx.pathParam("utente_id"));
        try{
            var feedbacks = feedbackService.getFeedbacksByUtente(utente_id);
            ctx.status(HttpStatus.OK).json(feedbacks);
            log.info("Feedbacks recuperati con successo per utente ID {}: {}", utente_id, feedbacks.size());
        } catch (FeedbackNotFoundException e){
            log.error("Errore nel recupero dei feedbacks per utente ID {}: {}", utente_id, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getFeedbacksByStruttura(Context ctx){
        log.info("GET /api/v1/feedbacks/struttura/{} - Richiesta recupero feedback per ID prenotazione", ctx.pathParam("prenotazione_id"));
        Long prenotazione_id = Long.valueOf(ctx.pathParam("prenotazione_id"));
        try{
            var feedbacks = feedbackService.getFeedbacksByStruttura(prenotazione_id);
            ctx.status(HttpStatus.OK).json(feedbacks);
            log.info("Feedbacks recuperati con successo per prenotazione ID {}: {}", prenotazione_id, feedbacks.size());
        } catch (FeedbackNotFoundException e){
            log.error("Errore nel recupero dei feedbacks per prenotazione ID {}: {}", prenotazione_id, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getFeedbackByUtenteAndPrenotazione(Context ctx){
        log.info("GET /api/v1/feedbacks/utente/{}/prenotazione/{} - Richiesta recupero feedback per ID utente e ID prenotazione",
                 ctx.pathParam("utente_id"), ctx.pathParam("prenotazione_id"));
        Long utente_id = Long.valueOf(ctx.pathParam("utente_id"));
        Long prenotazione_id = Long.valueOf(ctx.pathParam("prenotazione_id"));
        try{
            Feedback feedback = feedbackService.getFeedbackByUtenteAndPrenotazione(utente_id, prenotazione_id);
            ctx.status(HttpStatus.OK).json(feedback);
            log.info("Feedback recuperato con successo per utente ID {} e prenotazione ID {}: {}", utente_id, prenotazione_id, feedback);
        } catch (FeedbackNotFoundException e){
            log.error("Errore nel recupero del feedback per utente ID {} e prenotazione ID {}: {}", utente_id, prenotazione_id, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

// ==================== UPDATE ====================

    private void updateFeedbackComment(Context ctx){
        log.info("PUT /api/v1/feedbacks/{} - Richiesta aggiornamento commento feedback", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        String newComment = ctx.bodyAsClass(String.class);
        try{
            Feedback feedback = feedbackService.getFeedbackById(id);
            Feedback updatedFeedback = feedbackService.updateFeedbackComment(feedback, newComment);
            ctx.status(HttpStatus.OK).json(updatedFeedback);
            log.info("Feedback aggiornato con successo: {}", updatedFeedback);
        } catch (FeedbackNotFoundException e){
            log.error("Errore nell'aggiornamento del feedback: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

// ==================== DELETE ====================

    private void deleteAllFeedbacks(Context ctx){
        log.info("DELETE /api/v1/feedbacks - Richiesta eliminazione di tutti i feedback");
        try{
            int deletedCount = feedbackService.deleteAllFeedbacks();
            ctx.status(HttpStatus.OK).result("Totale feedback eliminati: " + deletedCount);
            log.info("Tutti i feedback eliminati con successo, totale: {}", deletedCount);
        } catch (Exception e){
            log.error("Errore nell'eliminazione dei feedbacks: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    private void deleteFeedback(Context ctx){
        log.info("DELETE /api/v1/feedbacks/{} - Richiesta eliminazione feedback", ctx.pathParam("id"));
        Long id = Long.valueOf(ctx.pathParam("id"));
        try{
            boolean deleted = feedbackService.deleteFeedbackById(id);
            if(deleted){
                ctx.status(HttpStatus.OK).result("Feedback eliminato con successo con ID: " + id);
                log.info("Feedback eliminato con successo con ID: {}", id);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Feedback non trovato con ID: " + id);
                log.warn("Feedback non trovato con ID: {}", id);
            }
        } catch (FeedbackNotFoundException e){
            log.error("Errore nell'eliminazione del feedback: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }
    
}
