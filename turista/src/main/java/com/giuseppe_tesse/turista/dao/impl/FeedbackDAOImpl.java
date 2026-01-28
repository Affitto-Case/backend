package com.giuseppe_tesse.turista.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.dao.FeedbackDAO;
import com.giuseppe_tesse.turista.exception.FeedbackNotFoundException;
import com.giuseppe_tesse.turista.exception.DuplicateFeedbackException;
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
        String sql = "INSERT INTO feedbacks (title, text, rating, booking_id, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, feedback.getTitle());
            ps.setString(2, feedback.getComment());
            ps.setInt(3, feedback.getRating());
            ps.setLong(4, feedback.getBooking().getId());
            ps.setLong(5, feedback.getUser().getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    feedback.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating feedback failed, no ID obtained.");
                }
            }

            log.info("Feedback created with ID: " + feedback.getId());
            return feedback;

        } catch (DuplicateFeedbackException e) {
            log.error("Duplicate feedback for booking: " + feedback.getBooking().getId(), e);
            throw new DuplicateFeedbackException("Feedback already exists for this booking.");
        } catch (SQLException e) {
            log.error("Error creating feedback", e);
            throw new RuntimeException("Error creating feedback", e);
        }
    }

// ==================== READ ====================

    @Override
    public List<Feedback> findAll() {
        String sql = "SELECT * FROM feedbacks";
        List<Feedback> feedbacks = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                feedbacks.add(mapResultSetToFeedback(rs));
            }

            log.info("Retrieved " + feedbacks.size() + " feedbacks.");
            return feedbacks;

        } catch (SQLException e) {
            log.error("Error retrieving feedbacks", e);
            throw new RuntimeException("Error retrieving feedbacks", e);
        }
    }

    @Override
    public Optional<Feedback> findById(Long id) {
        String sql = "SELECT * FROM feedbacks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFeedback(rs));
                }
            } catch (FeedbackNotFoundException e) {
                log.error("Feedback not found with ID: " + id, e);
                throw new FeedbackNotFoundException("Feedback not found with ID: " + id);
            }

        } catch (SQLException e) {
            log.error("Error finding feedback by ID: " + id, e);
            throw new RuntimeException("Error finding feedback by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Feedback> findByUserId(Long userId) {
        String sql = "SELECT * FROM feedbacks WHERE user_id = ?";
        List<Feedback> feedbacks = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    feedbacks.add(mapResultSetToFeedback(rs));
                }
            }
            return feedbacks;

        } catch (SQLException e) {
            log.error("Error finding feedbacks for user ID: " + userId, e);
            throw new RuntimeException(e);
        }
    }

        @Override
    public Optional<List<Feedback>> findByBookingId(Long bookingId) {
        String sql = "SELECT * FROM feedbacks WHERE booking_id = ?";
        List<Feedback> feedbacks = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setLong(1, bookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    feedbacks.add(mapResultSetToFeedback(rs));
                }
            }
            
            return feedbacks.isEmpty() ? Optional.empty() : Optional.of(feedbacks);
    
        } catch (SQLException e) {
            log.error("Error finding feedbacks for booking ID: " + bookingId, e);
            throw new RuntimeException("Error finding feedbacks by booking ID", e);
        }
    }

        @Override
    public Optional<Feedback> findByUserIdAndBookingId(Long userId, Long bookingId) {
        String sql = "SELECT * FROM feedbacks WHERE user_id = ? AND booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            pstmt.setLong(2, bookingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFeedback(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error finding feedback for user " + userId + " and booking " + bookingId, e);
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }



// ==================== UPDATE ====================

    @Override
    public Optional<Feedback> updateComment(Feedback feedback) {
        String sql = "UPDATE feedbacks SET title = ?, text = ?, rating = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, feedback.getTitle());
            ps.setString(2, feedback.getComment());
            ps.setInt(3, feedback.getRating());
            ps.setLong(4, feedback.getId());

            int rows = ps.executeUpdate();
            return rows > 0 ? Optional.of(feedback) : Optional.empty();

        } catch (SQLException e) {
            log.error("Error updating feedback ID: " + feedback.getId(), e);
            throw new RuntimeException(e);
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
                log.info("Feedback deleted with ID: " + id);
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            log.error("Error deleting feedback with ID: " + id, e);
            throw new RuntimeException("Error deleting feedback with ID: " + id, e);
        }
    }

    @Override
    public int deleteAll() {
        String sql = "DELETE FROM feedbacks";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int rowsAffected = ps.executeUpdate();
            log.info("Deleted " + rowsAffected + " feedbacks.");
            return rowsAffected;

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
                log.info("Feedback deleted for user: " + userId + " and booking: " + bookingId);
                return true;
            } else {
                log.debug("No feedback found to delete for user: " + userId + " and booking: " + bookingId);
                return false;
            }

        } catch (SQLException e) {
            log.error("Error deleting feedback for user: " + userId, e);
            throw new RuntimeException("Error deleting feedback", e);
        }
    }


    
    // ==================== UTILITY ====================
    
    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(rs.getLong("id"));
        feedback.setTitle(rs.getString("title"));
        feedback.setComment(rs.getString("text"));
        feedback.setRating(rs.getInt("rating"));
        
        // Mapping degli ID relazionali (User e Booking)
        User user = new User();
        user.setId(rs.getLong("user_id"));
        feedback.setUser(user);
        
        Booking booking = new Booking();
        booking.setId(rs.getLong("booking_id"));
        feedback.setBooking(booking);
        
        return feedback;
    }

}