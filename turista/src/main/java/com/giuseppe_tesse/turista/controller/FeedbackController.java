package com.giuseppe_tesse.turista.controller;

import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.model.Feedback;
import com.giuseppe_tesse.turista.model.User;
import com.giuseppe_tesse.turista.service.BookingService;
import com.giuseppe_tesse.turista.service.FeedbackService;
import com.giuseppe_tesse.turista.service.UserService;
import com.giuseppe_tesse.turista.dto.request.FeedbackRequestDTO;
import com.giuseppe_tesse.turista.dto.response.FeedbackResponseDTO;
import com.giuseppe_tesse.turista.dto.mapper.FeedbackMapper;
import com.giuseppe_tesse.turista.exception.FeedbackNotFoundException;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FeedbackController implements Controller {

    private final FeedbackService feedbackService;
    private final BookingService bookingService;
    private final UserService userService;

    public FeedbackController(FeedbackService feedbackService, BookingService bookingService, UserService userService) {
        this.feedbackService = feedbackService;
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.post("/api/v1/feedbacks", this::createFeedback);
        app.get("/api/v1/feedbacks/{id}", this::getFeedbackById);
        app.get("/api/v1/feedbacks", this::getAllFeedbacks);
        app.get("/api/v1/feedbacks/user/{userId}", this::getFeedbacksByUser);
        app.get("/api/v1/feedbacks/booking/{bookingId}", this::getFeedbacksByBooking);
        app.get("/api/v1/feedbacks/user/{userId}/booking/{bookingId}", this::getFeedbackByUserAndBooking);
        app.get("/api/v1/feedbacks/stats/count", this::getFeedbackCount);
        app.put("/api/v1/feedbacks/{id}", this::updateFeedback);
        app.delete("/api/v1/feedbacks", this::deleteAllFeedbacks);
        app.delete("/api/v1/feedbacks/{id}", this::deleteFeedbackById);
    }

    // ==================== CREATE ====================
    private void createFeedback(Context ctx) {
        log.info("POST /api/v1/feedbacks - Request to create feedback");

        try {
            FeedbackRequestDTO requestDTO = ctx.bodyAsClass(FeedbackRequestDTO.class);

            Booking booking = bookingService.getBookingById(requestDTO.getBookingId());
            User user = userService.getUserById(requestDTO.getUserId());

            Feedback feedback = FeedbackMapper.toEntity(requestDTO, user, booking);

            Feedback createdFeedback = feedbackService.createFeedback(feedback);

            FeedbackResponseDTO responseDTO = FeedbackMapper.toResponseDTO(createdFeedback);

            ctx.status(HttpStatus.CREATED).json(responseDTO);
            log.info("Feedback created successfully with ID: {}", createdFeedback.getId());

        } catch (Exception e) {
            log.error("Error creating feedback: {}", e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        }
    }

    // ==================== READ ====================
    private void getFeedbackById(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("GET /api/v1/feedbacks/{} - Request to fetch feedback by ID", id);

        try {
            Feedback feedback = feedbackService.getFeedbackById(id);
            FeedbackResponseDTO responseDTO = FeedbackMapper.toResponseDTO(feedback);
            ctx.status(HttpStatus.OK).json(responseDTO);
            log.info("Feedback retrieved successfully: ID {}", id);
        } catch (FeedbackNotFoundException e) {
            log.error("Feedback not found: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getAllFeedbacks(Context ctx) {
        log.info("GET /api/v1/feedbacks - Request to fetch all feedbacks");

        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
            List<FeedbackResponseDTO> responseDTOs = feedbacks.stream()
                    .map(FeedbackMapper::toResponseDTO)
                    .collect(Collectors.toList());
            ctx.status(HttpStatus.OK).json(responseDTOs);
            log.info("All feedbacks retrieved successfully, total: {}", responseDTOs.size());
        } catch (Exception e) {
            log.error("Error fetching feedbacks: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    private void getFeedbackCount(Context ctx) {
        log.info("GET /api/v1/feedbacks/stats/count");
        ctx.status(HttpStatus.OK).json(feedbackService.getFeedbackCount());
    }

    private void getFeedbacksByUser(Context ctx) {
        Long userId = Long.valueOf(ctx.pathParam("userId"));
        log.info("GET /api/v1/feedbacks/user/{} - Request to fetch feedbacks by user ID", userId);

        try {
            List<Feedback> feedbacks = feedbackService.getFeedbacksByUser(userId);
            List<FeedbackResponseDTO> responseDTOs = feedbacks.stream()
                    .map(FeedbackMapper::toResponseDTO)
                    .collect(Collectors.toList());
            ctx.status(HttpStatus.OK).json(responseDTOs);
            log.info("Feedbacks retrieved successfully for user ID {}, total: {}", userId, responseDTOs.size());
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

            // ✅ CORRETTO - Converte a DTO
            List<FeedbackResponseDTO> responseDTOs = feedbacks.stream()
                    .map(FeedbackMapper::toResponseDTO)
                    .collect(Collectors.toList());

            ctx.status(HttpStatus.OK).json(responseDTOs);
            log.info("Feedbacks retrieved successfully for booking ID {}, total: {}", bookingId, responseDTOs.size());
        } catch (FeedbackNotFoundException e) {
            log.error("Feedbacks not found for booking ID {}: {}", bookingId, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    private void getFeedbackByUserAndBooking(Context ctx) {
        Long userId = Long.valueOf(ctx.pathParam("userId"));
        Long bookingId = Long.valueOf(ctx.pathParam("bookingId"));
        log.info("GET /api/v1/feedbacks/user/{}/booking/{} - Request to fetch feedback by user and booking", userId,
                bookingId);

        try {
            Feedback feedback = feedbackService.getFeedbackByUserAndBooking(userId, bookingId);

            // ✅ CORRETTO - Converte a DTO
            FeedbackResponseDTO responseDTO = FeedbackMapper.toResponseDTO(feedback);

            ctx.status(HttpStatus.OK).json(responseDTO);
            log.info("Feedback retrieved successfully for user ID {} and booking ID {}", userId, bookingId);
        } catch (FeedbackNotFoundException e) {
            log.error("Feedback not found for user ID {} and booking ID {}: {}", userId, bookingId, e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    // ==================== UPDATE ====================
    private void updateFeedback(Context ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        log.info("PUT /api/v1/feedbacks/{} - Request to update feedback", id);

        try {
            FeedbackRequestDTO requestDTO = ctx.bodyAsClass(FeedbackRequestDTO.class);

            Feedback existingFeedback = feedbackService.getFeedbackById(id);

            if (requestDTO.getTitle() != null) {
                existingFeedback.setTitle(requestDTO.getTitle());
            }
            if (requestDTO.getRating() != null) {
                existingFeedback.setRating(requestDTO.getRating());
            }
            if (requestDTO.getComment() != null) {
                existingFeedback.setComment(requestDTO.getComment());
            }

            Feedback updatedFeedback = feedbackService.updateFeedback(existingFeedback);

            FeedbackResponseDTO responseDTO = FeedbackMapper.toResponseDTO(updatedFeedback);

            ctx.status(HttpStatus.OK).json(responseDTO);
            log.info("Feedback updated successfully: ID {}", id);

        } catch (FeedbackNotFoundException e) {
            log.error("Feedback not found for update: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating feedback: {}", e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request data");
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