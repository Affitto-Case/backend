package com.giuseppe_tesse.turista.controller;

import com.giuseppe_tesse.turista.model.Feedback;
import com.giuseppe_tesse.turista.service.FeedbackService;
import com.giuseppe_tesse.turista.exception.FeedbackNotFoundException;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class FeedbackController implements Controller {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/feedbacks", this::createFeedback);
        app.get("/api/v1/feedbacks/{id}", this::getFeedbackById);
        app.get("/api/v1/feedbacks", this::getAllFeedbacks);
        app.get("/api/v1/feedbacks/user/{userId}", this::getFeedbacksByUser);
        app.get("/api/v1/feedbacks/booking/{bookingId}", this::getFeedbacksByBooking); // Rinominato endpoint
        app.get("/api/v1/feedbacks/user/{userId}/booking/{bookingId}", this::getFeedbackByUserAndBooking); // Rinominato endpoint
        app.put("/api/v1/feedbacks/{id}", this::updateFeedbackComment);
        app.delete("/api/v1/feedbacks", this::deleteAllFeedbacks);
        app.delete("/api/v1/feedbacks/{id}", this::deleteFeedbackById);
    }

    // ==================== CREATE ====================
    private void createFeedback(Context ctx) {
        log.info("POST /api/v1/feedbacks - Request to create feedback");
        Feedback feedback = ctx.bodyAsClass(Feedback.class);

        try {
            Feedback createdFeedback = feedbackService.createFeedback(
                    feedback.getBooking(),
                    feedback.getUser(),
                    feedback.getTitle(),
                    feedback.getRating(),
                    feedback.getComment()
            );
            ctx.status(HttpStatus.CREATED).json(createdFeedback);
            log.info("Feedback created successfully: {}", createdFeedback);
        } catch (Exception e) {
            log.error("Error creating feedback: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    // ==================== READ ====================
    private void getFeedbackById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("GET /api/v1/feedbacks/{} - Request to fetch feedback by ID", id);

        try {
            Feedback feedback = feedbackService.getFeedbackById(id);
            ctx.status(HttpStatus.OK).json(feedback);
            log.info("Feedback retrieved successfully: {}", feedback);
        } catch (FeedbackNotFoundException e) {
            log.error("Feedback not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getAllFeedbacks(Context ctx) {
        log.info("GET /api/v1/feedbacks - Request to fetch all feedbacks");
        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
            ctx.status(HttpStatus.OK).json(feedbacks);
            log.info("All feedbacks retrieved successfully, total: {}", feedbacks.size());
        } catch (Exception e) {
            log.error("Error fetching feedbacks: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    private void getFeedbacksByUser(Context ctx) {
        Long userId = Long.valueOf(ctx.pathParam("userId"));
        log.info("GET /api/v1/feedbacks/user/{} - Request to fetch feedbacks by user ID", userId);

        try {
            List<Feedback> feedbacks = feedbackService.getFeedbacksByUser(userId);
            ctx.status(HttpStatus.OK).json(feedbacks);
            log.info("Feedbacks retrieved successfully for user ID {}: count {}", userId, feedbacks.size());
        } catch (FeedbackNotFoundException e) {
            log.error("Feedbacks not found for user ID {}: {}", userId, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getFeedbacksByBooking(Context ctx) {
        Long bookingId = Long.valueOf(ctx.pathParam("bookingId"));
        log.info("GET /api/v1/feedbacks/booking/{} - Request to fetch feedbacks by booking ID", bookingId);

        try {
            List<Feedback> feedbacks = feedbackService.getFeedbacksByBooking(bookingId);
            ctx.status(HttpStatus.OK).json(feedbacks);
            log.info("Feedbacks retrieved successfully for booking ID {}: count {}", bookingId, feedbacks.size());
        } catch (FeedbackNotFoundException e) {
            log.error("Feedbacks not found for booking ID {}: {}", bookingId, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getFeedbackByUserAndBooking(Context ctx) {
        Long userId = Long.valueOf(ctx.pathParam("userId"));
        Long bookingId = Long.valueOf(ctx.pathParam("bookingId"));
        log.info("GET /api/v1/feedbacks/user/{}/booking/{} - Request to fetch feedback by user and booking", userId, bookingId);

        try {
            Feedback feedback = feedbackService.getFeedbackByUserAndBooking(userId, bookingId);
            ctx.status(HttpStatus.OK).json(feedback);
            log.info("Feedback retrieved successfully for user ID {} and booking ID {}: {}", userId, bookingId, feedback);
        } catch (FeedbackNotFoundException e) {
            log.error("Feedback not found for user ID {} and booking ID {}: {}", userId, bookingId, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    // ==================== UPDATE ====================

    private void updateFeedbackComment(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("PUT /api/v1/feedbacks/{} - Request to update feedback comment", id);
        
        // Leggiamo il nuovo commento dal corpo della richiesta
        String newComment = ctx.body(); 

        try {
            // 1. Recuperiamo l'oggetto feedback esistente tramite ID
            Feedback feedback = feedbackService.getFeedbackById(id);
            
            // 2. Chiamiamo il service passando sia l'oggetto che la stringa (come richiesto dalla firma)
            Feedback updatedFeedback = feedbackService.updateFeedbackComment(feedback, newComment);
            
            ctx.status(HttpStatus.OK).json(updatedFeedback);
            log.info("Feedback updated successfully: {}", updatedFeedback);
        } catch (FeedbackNotFoundException e) {
            log.error("Feedback not found for update: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        } catch (Exception e) {
            log.error("Error during feedback update: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error updating feedback");
        }
    }

    // ==================== DELETE ====================
    private void deleteAllFeedbacks(Context ctx) {
        log.info("DELETE /api/v1/feedbacks - Request to delete all feedbacks");

        try {
            feedbackService.deleteAllFeedbacks();
            ctx.status(HttpStatus.NO_CONTENT);
            log.info("All feedbacks deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting all feedbacks: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    private void deleteFeedbackById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("DELETE /api/v1/feedbacks/{} - Request to delete feedback", id);

        try {
            feedbackService.deleteFeedbackById(id);
            ctx.status(HttpStatus.NO_CONTENT);
            log.info("Feedback deleted successfully with ID: {}", id);
        } catch (FeedbackNotFoundException e) {
            log.error("Error deleting feedback: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }
}