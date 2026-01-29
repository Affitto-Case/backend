package com.giuseppe_tesse.turista.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.dao.FeedbackDAO;
import com.giuseppe_tesse.turista.model.Feedback;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.model.User;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeedbackDAOImpl implements FeedbackDAO {

// ==================== CREATE ====================

    @Override
    public Feedback create(Feedback feedback) {
        String sql = "INSERT INTO feedbacks (title, comment, rating, booking_id, user_id) VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, feedback.getTitle());
            ps.setString(2, feedback.getComment());
            ps.setInt(3, feedback.getRating());
            ps.setLong(4, feedback.getBookingId());
            ps.setLong(5, feedback.getUserId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    feedback.setId(rs.getLong("id"));
                } else {
                    throw new SQLException("Creating feedback failed, no ID obtained.");
                }
            }

            log.info("Feedback created with ID: {}", feedback.getId());
            return feedback;

        } catch (SQLException e) {
            log.error("Error creating feedback for booking ID: {}", feedback.getBookingId(), e);
            throw new RuntimeException("Error creating feedback", e);
        }
    }

// ==================== READ ====================

    @Override
    public List<Feedback> findAll() {
        String sql = "SELECT id, title, comment, rating, booking_id, user_id FROM feedbacks";
        List<Feedback> feedbacks = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                feedbacks.add(mapResultSetToFeedback(rs));
            }

            log.info("Retrieved {} feedbacks", feedbacks.size());
            return feedbacks;

        } catch (SQLException e) {
            log.error("Error retrieving feedbacks", e);
            throw new RuntimeException("Error retrieving feedbacks", e);
        }
    }

    @Override
    public Optional<Feedback> findById(Long id) {
        String sql = "SELECT id, title, comment, rating, booking_id, user_id FROM feedbacks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFeedback(rs));
                }
            }

            log.debug("No feedback found with ID: {}", id);
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error finding feedback by ID: {}", id, e);
            throw new RuntimeException("Error finding feedback by ID", e);
        }
    }

    @Override
    public List<Feedback> findByUserId(Long userId) {
        String sql = "SELECT id, title, comment, rating, booking_id, user_id FROM feedbacks WHERE user_id = ?";
        List<Feedback> feedbacks = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    feedbacks.add(mapResultSetToFeedback(rs));
                }
            }
            
            log.info("Retrieved {} feedbacks for user ID: {}", feedbacks.size(), userId);
            return feedbacks;

        } catch (SQLException e) {
            log.error("Error finding feedbacks for user ID: {}", userId, e);
            throw new RuntimeException("Error finding feedbacks by user ID", e);
        }
    }

    @Override
    public Optional<List<Feedback>> findByBookingId(Long bookingId) {
        String sql = "SELECT id, title, comment, rating, booking_id, user_id FROM feedbacks WHERE booking_id = ?";
        List<Feedback> feedbacks = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
    
            ps.setLong(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    feedbacks.add(mapResultSetToFeedback(rs));
                }
            }
            
            if (feedbacks.isEmpty()) {
                log.debug("No feedbacks found for booking ID: {}", bookingId);
                return Optional.empty();
            }
            
            log.info("Retrieved {} feedbacks for booking ID: {}", feedbacks.size(), bookingId);
            return Optional.of(feedbacks);
    
        } catch (SQLException e) {
            log.error("Error finding feedbacks for booking ID: {}", bookingId, e);
            throw new RuntimeException("Error finding feedbacks by booking ID", e);
        }
    }

    @Override
    public Optional<Feedback> findByUserIdAndBookingId(Long userId, Long bookingId) {
        String sql = "SELECT id, title, comment, rating, booking_id, user_id FROM feedbacks WHERE user_id = ? AND booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFeedback(rs));
                }
            }
            
            log.debug("No feedback found for user ID: {} and booking ID: {}", userId, bookingId);
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("Error finding feedback for user ID: {} and booking ID: {}", userId, bookingId, e);
            throw new RuntimeException("Error finding feedback", e);
        }
    }

// ==================== UPDATE ====================

    @Override
    public Optional<Feedback> updateComment(Feedback feedback) {
        String sql = "UPDATE feedbacks SET title = ?, comment = ?, rating = ? WHERE id = ? RETURNING id, title, comment, rating, booking_id, user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, feedback.getTitle());
            ps.setString(2, feedback.getComment());
            ps.setInt(3, feedback.getRating());
            ps.setLong(4, feedback.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFeedback(rs));
                }
            }

            log.debug("No feedback updated with ID: {}", feedback.getId());
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error updating feedback ID: {}", feedback.getId(), e);
            throw new RuntimeException("Error updating feedback", e);
        }
    }

// ==================== DELETE ====================

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM feedbacks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("Feedback deleted with ID: {}", id);
                return true;
            }

            log.debug("No feedback deleted with ID: {}", id);
            return false;

        } catch (SQLException e) {
            log.error("Error deleting feedback with ID: {}", id, e);
            throw new RuntimeException("Error deleting feedback", e);
        }
    }

    @Override
    public int deleteAll() {
        String sql = "DELETE FROM feedbacks";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int deleted = ps.executeUpdate();
            log.info("Deleted {} feedbacks", deleted);
            return deleted;

        } catch (SQLException e) {
            log.error("Error deleting all feedbacks", e);
            throw new RuntimeException("Error deleting all feedbacks", e);
        }
    }

    @Override
    public boolean deleteByUserIdAndBookingId(Long userId, Long bookingId) {
        String sql = "DELETE FROM feedbacks WHERE user_id = ? AND booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, bookingId);
            
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("Feedback deleted for user ID: {} and booking ID: {}", userId, bookingId);
                return true;
            }

            log.debug("No feedback deleted for user ID: {} and booking ID: {}", userId, bookingId);
            return false;

        } catch (SQLException e) {
            log.error("Error deleting feedback for user ID: {} and booking ID: {}", userId, bookingId, e);
            throw new RuntimeException("Error deleting feedback", e);
        }
    }

// ==================== UTILITY ====================
    
    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(rs.getLong("id"));
        feedback.setTitle(rs.getString("title"));
        feedback.setComment(rs.getString("comment"));
        feedback.setRating(rs.getInt("rating"));
        
        // Mapping degli ID relazionali (User e Booking)
        User user = new User();
        user.setId(rs.getLong("user_id"));
        feedback.setUserId(user.getId());
        
        Booking booking = new Booking();
        booking.setId(rs.getLong("booking_id"));
        feedback.setBookingId(booking.getId());
        
        return feedback;
    }
}