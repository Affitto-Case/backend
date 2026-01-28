package com.giuseppe_tesse.turista.service;

import java.util.List;

import com.giuseppe_tesse.turista.dao.FeedbackDAO;
import com.giuseppe_tesse.turista.model.Feedback;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.exception.FeedbackNotFoundException;
import com.giuseppe_tesse.turista.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeedbackService {

    private final FeedbackDAO feedbackDAO;

    public FeedbackService(FeedbackDAO feedbackDAO) {
        this.feedbackDAO = feedbackDAO;
    }

    // ==================== CREATE ====================

    public Feedback createFeedback(Booking booking,User user, String title, int rating, String comment) {
        log.info("Attempt to insert feedback - Booking ID: {}, Title: {}", booking.getId(), title);
        Feedback feedback = new Feedback(booking,user, title, rating, comment);
        return feedbackDAO.create(feedback);
    }

    // ==================== READ ====================

    public Feedback getFeedbackById(Long id) {
        log.info("Fetching feedback by ID: {}", id);
        return feedbackDAO.findById(id)
                .orElseThrow(() -> {
                    log.warn("Feedback not found with ID: {}", id);
                    return new FeedbackNotFoundException(id);
                });
    }

    public List<Feedback> getAllFeedbacks() {
        log.info("Fetching all feedbacks");
        return feedbackDAO.findAll();
    }

    public List<Feedback> getFeedbacksByUser(Long userId) {
        log.info("Fetching feedbacks by User ID: {}", userId);
        return feedbackDAO.findByUserId(userId);
    }

    public List<Feedback> getFeedbacksByBooking(Long bookingId) {
        log.info("Fetching feedbacks by Booking ID: {}", bookingId);
        return feedbackDAO.findByBookingId(bookingId)
                .orElseThrow(() -> {
                    log.warn("No feedbacks found for Booking ID: {}", bookingId);
                    return new FeedbackNotFoundException("No feedback found for Booking ID: " + bookingId);
                });
    }

    public Feedback getFeedbackByUserAndBooking(Long userId, Long bookingId) {
        log.info("Fetching feedback by User ID: {} and Booking ID: {}", userId, bookingId);
        return feedbackDAO.findByUserIdAndBookingId(userId, bookingId)
                .orElseThrow(() -> {
                    log.warn("Feedback not found for User ID: {} and Booking ID: {}", userId, bookingId);
                    return new FeedbackNotFoundException("Feedback not found for User ID: " + userId +
                                                        " and Booking ID: " + bookingId);
                });
    }

    // ==================== UPDATE ====================

    public Feedback updateFeedbackComment(Feedback feedback, String newComment) {
        log.info("Updating feedback comment for ID: {}", feedback.getId());
        feedback.setComment(newComment);
        return feedbackDAO.updateComment(feedback)
                .orElseThrow(() -> {
                    log.warn("Failed to update comment for Feedback ID: {}", feedback.getId());
                    return new FeedbackNotFoundException(feedback.getId());
                });
    }

    // ==================== DELETE ====================

    public int deleteAllFeedbacks() {
        log.info("Deleting all feedbacks");
        return feedbackDAO.deleteAll();
    }

    public boolean deleteFeedbackById(Long id) {
        log.info("Attempting to delete feedback with ID: {}", id);
        boolean deleted = feedbackDAO.deleteById(id);
        if (!deleted) {
            log.warn("Failed to delete feedback - ID not found: {}", id);
            throw new FeedbackNotFoundException(id);
        }
        return true;
    }

    public boolean deleteFeedback(Long userId, Long bookingId) {
        log.info("Attempting to delete feedback for User ID: {} and Booking ID: {}", userId, bookingId);
        boolean deleted = feedbackDAO.deleteByUserIdAndBookingId(userId, bookingId);
        if (!deleted) {
            log.warn("Failed to delete feedback - not found for User ID: {} and Booking ID: {}", userId, bookingId);
            throw new FeedbackNotFoundException("Feedback not found for User ID: " + userId +
                                                " and Booking ID: " + bookingId);
        }
        return true;
    }
}
