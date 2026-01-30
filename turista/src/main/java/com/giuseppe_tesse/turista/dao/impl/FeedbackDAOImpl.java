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
import com.giuseppe_tesse.turista.model.Residence;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.model.User;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import com.giuseppe_tesse.turista.util.DateConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeedbackDAOImpl implements FeedbackDAO {

    private final String ALL_JOIN =
        "SELECT \r\n" +
        "    f.id AS id, \r\n" +
        "    f.title AS title, \r\n" +
        "    f.comment AS comment, \r\n" +
        "    f.rating AS rating, \r\n" +
        "\r\n" +
        "    u.id AS user_id, \r\n" +
        "    u.first_name AS first_name, \r\n" +
        "    u.last_name AS last_name, \r\n" +
        "    u.email AS email, \r\n" +
        "\r\n" +
        "    b.id AS booking_id, \r\n" +
        "    b.start_date AS start_date, \r\n" +
        "    b.end_date AS end_date, \r\n" +
        "\r\n" +
        "    r.id AS residence_id, \r\n" +
        "    r.name AS residence_name, \r\n" +
        "    r.address AS residence_address \r\n" +
        "\r\n" +
        "FROM feedbacks f \r\n" +
        "JOIN bookings b ON f.booking_id = b.id \r\n" +
        "JOIN users u ON f.user_id = u.id \r\n" +
        "JOIN residences r ON b.residence_id = r.id";


// ==================== CREATE ====================

    @Override
    public Feedback create(Feedback feedback) {
        log.info("Creating new feedback for booking ID: {} by user ID: {}", feedback.getBooking().getId(), feedback.getUser().getId());
        String sql = "INSERT INTO feedbacks (title, comment, rating, booking_id, user_id) VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, feedback.getTitle());
            ps.setString(2, feedback.getComment());
            ps.setInt(3, feedback.getRating());
            ps.setLong(4, feedback.getBooking().getId());
            ps.setLong(5, feedback.getUser().getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    feedback.setId(rs.getLong("id"));
                    log.info("Feedback created successfully with ID: {}", feedback.getId());
                } else {
                    log.error("Creating feedback failed, no ID obtained");
                    throw new SQLException("Creating feedback failed, no ID obtained.");
                }
            }

            return feedback;

        } catch (SQLException e) {
            log.error("Error creating feedback for booking ID: {} by user ID: {}", feedback.getBooking().getId(), feedback.getUser().getId(), e);
            throw new RuntimeException("Error creating feedback", e);
        }
    }

// ==================== READ ====================

    @Override
    public List<Feedback> findAll() {
        log.info("Retrieving all feedbacks");
        String sql = ALL_JOIN;
        List<Feedback> feedbacks = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                feedbacks.add(mapResultSetToFeedback(rs));
            }

            log.info("Successfully retrieved {} feedbacks", feedbacks.size());
            return feedbacks;

        } catch (SQLException e) {
            log.error("Error retrieving all feedbacks", e);
            throw new RuntimeException("Error retrieving feedbacks", e);
        }
    }

    @Override
    public Optional<Feedback> findById(Long id) {
        log.info("Finding feedback by ID: {}", id);
        String sql = ALL_JOIN +" WHERE f.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Feedback feedback = mapResultSetToFeedback(rs);
                    log.info("Successfully found feedback with ID: {}", id);
                    return Optional.of(feedback);
                }
            }

            log.warn("No feedback found with ID: {}", id);
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error finding feedback by ID: {}", id, e);
            throw new RuntimeException("Error finding feedback by ID", e);
        }
    }

    @Override
    public List<Feedback> findByUserId(Long userId) {
        log.info("Finding feedbacks for user ID: {}", userId);
        String sql = ALL_JOIN+" WHERE u.id = ?";
        List<Feedback> feedbacks = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    feedbacks.add(mapResultSetToFeedback(rs));
                }
            }
            
            log.info("Successfully retrieved {} feedbacks for user ID: {}", feedbacks.size(), userId);
            return feedbacks;

        } catch (SQLException e) {
            log.error("Error finding feedbacks for user ID: {}", userId, e);
            throw new RuntimeException("Error finding feedbacks by user ID", e);
        }
    }

    @Override
    public Optional<List<Feedback>> findByBookingId(Long bookingId) {
        log.info("Finding feedbacks for booking ID: {}", bookingId);
        String sql = ALL_JOIN+" WHERE b.id = ?";
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
                log.info("No feedbacks found for booking ID: {}", bookingId);
                return Optional.empty();
            }
            
            log.info("Successfully retrieved {} feedbacks for booking ID: {}", feedbacks.size(), bookingId);
            return Optional.of(feedbacks);
    
        } catch (SQLException e) {
            log.error("Error finding feedbacks for booking ID: {}", bookingId, e);
            throw new RuntimeException("Error finding feedbacks by booking ID", e);
        }
    }

    @Override
    public Optional<Feedback> findByUserIdAndBookingId(Long userId, Long bookingId) {
        log.info("Finding feedback for user ID: {} and booking ID: {}", userId, bookingId);
        String sql = ALL_JOIN+" WHERE u.id = ? AND b.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Feedback feedback = mapResultSetToFeedback(rs);
                    log.info("Successfully found feedback for user ID: {} and booking ID: {}", userId, bookingId);
                    return Optional.of(feedback);
                }
            }
            
            log.warn("No feedback found for user ID: {} and booking ID: {}", userId, bookingId);
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("Error finding feedback for user ID: {} and booking ID: {}", userId, bookingId, e);
            throw new RuntimeException("Error finding feedback", e);
        }
    }

// ==================== UPDATE ====================

    @Override
    public Optional<Feedback> update(Feedback feedback) {
        log.info("Updating feedback with ID: {}", feedback.getId());
        String sql = "UPDATE feedbacks SET title = ?, comment = ?, rating = ? WHERE id = ? RETURNING id, title, comment, rating, booking_id, user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, feedback.getTitle());
            ps.setString(2, feedback.getComment());
            ps.setInt(3, feedback.getRating());
            ps.setLong(4, feedback.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Feedback updatedFeedback = mapResultSetToFeedback(rs);
                    log.info("Successfully updated feedback with ID: {}", feedback.getId());
                    return Optional.of(updatedFeedback);
                }
            }

            log.warn("No feedback updated with ID: {}", feedback.getId());
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error updating feedback with ID: {}", feedback.getId(), e);
            throw new RuntimeException("Error updating feedback", e);
        }
    }

// ==================== DELETE ====================

    @Override
    public boolean deleteById(Long id) {
        log.info("Deleting feedback with ID: {}", id);
        String sql = "DELETE FROM feedbacks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("Successfully deleted feedback with ID: {}", id);
                return true;
            }

            log.warn("No feedback deleted with ID: {}", id);
            return false;

        } catch (SQLException e) {
            log.error("Error deleting feedback with ID: {}", id, e);
            throw new RuntimeException("Error deleting feedback", e);
        }
    }

    @Override
    public int deleteAll() {
        log.info("Deleting all feedbacks");
        String sql = "DELETE FROM feedbacks";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int deleted = ps.executeUpdate();
            log.info("Successfully deleted {} feedbacks", deleted);
            return deleted;

        } catch (SQLException e) {
            log.error("Error deleting all feedbacks", e);
            throw new RuntimeException("Error deleting all feedbacks", e);
        }
    }

    @Override
    public boolean deleteByUserIdAndBookingId(Long userId, Long bookingId) {
        log.info("Deleting feedback for user ID: {} and booking ID: {}", userId, bookingId);
        String sql = "DELETE FROM feedbacks WHERE user_id = ? AND booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, bookingId);
            
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("Successfully deleted feedback for user ID: {} and booking ID: {}", userId, bookingId);
                return true;
            }

            log.warn("No feedback deleted for user ID: {} and booking ID: {}", userId, bookingId);
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
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        feedback.setUser(user);

        Residence residence = new Residence();
        residence.setId(rs.getLong("residence_id"));
        residence.setName(rs.getString("residence_name"));
        residence.setAddress(rs.getString("residence_address"));
        
        Booking booking = new Booking();
        booking.setId(rs.getLong("booking_id"));
        booking.setStartDate(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("start_date")));
        booking.setEndDate(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("end_date")));
        booking.setResidence(residence);
        feedback.setBooking(booking);
        
        return feedback;
    }
}