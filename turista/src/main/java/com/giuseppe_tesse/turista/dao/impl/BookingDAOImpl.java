package com.giuseppe_tesse.turista.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.dao.BookingDAO;
import com.giuseppe_tesse.turista.exception.DuplicateBookingException;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.model.Residence;
import com.giuseppe_tesse.turista.model.User;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import com.giuseppe_tesse.turista.util.DateConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingDAOImpl implements BookingDAO {

    private final String SELECT_ALL_JOIN = 
        "SELECT b.id, b.start_date, b.end_date, b.residence_id, b.user_id, " +
        "u.first_name, u.last_name, u.email, " +
        "r.name AS residence_name, r.address AS residence_address " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.id " +
        "JOIN residences r ON b.residence_id = r.id";

// ==================== CREATE ====================

    @Override
    public Booking create(Booking booking) {
        log.info("Creating new booking for user ID: {} and residence ID: {}", booking.getUserId(), booking.getResidenceId());
        String sql = "INSERT INTO bookings (start_date, end_date, residence_id, user_id) VALUES (?, ?, ?, ?) RETURNING id";
        
        if (existsOverlappingBooking(booking.getResidenceId(), booking.getStartDate(), booking.getEndDate())) {
            log.warn("Booking creation failed: overlapping booking exists for residence ID: {} between {} and {}", 
                     booking.getResidenceId(), booking.getStartDate(), booking.getEndDate());
            throw new DuplicateBookingException(
                "This residence is already booked for the selected dates"
            );
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, DateConverter.convertTimestampFromLocalDateTime(booking.getStartDate()));
            ps.setTimestamp(2, DateConverter.convertTimestampFromLocalDateTime(booking.getEndDate()));
            ps.setLong(3, booking.getResidenceId());
            ps.setLong(4, booking.getUserId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    booking.setId(rs.getLong("id"));
                    log.info("Booking created successfully with ID: {}", booking.getId());
                } else {
                    log.error("Creating booking failed, no ID obtained");
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            }

            return booking;

        } catch (SQLException e) {
            log.error("Error creating booking for user ID: {} and residence ID: {}", booking.getUserId(), booking.getResidenceId(), e);
            throw new RuntimeException("Error creating booking", e);
        }
    }

// ==================== READ ====================

    @Override
    public Optional<Booking> findById(Long id) {
        log.info("Finding booking by ID: {}", id);
        String sql = SELECT_ALL_JOIN + " WHERE b.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    log.info("Successfully found booking with ID: {}", id);
                    return Optional.of(booking);
                }
            }
            
            log.warn("No booking found with ID: {}", id);
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error finding booking by ID: {}", id, e);
            throw new RuntimeException("Error finding booking by ID", e);
        }
    }

    @Override
    public List<Booking> findAll() {
        log.info("Retrieving all bookings");
        String sql = SELECT_ALL_JOIN;
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            log.info("Successfully retrieved {} bookings", bookings.size());
            return bookings;

        } catch (SQLException e) {
            log.error("Error retrieving all bookings", e);
            throw new RuntimeException("Error retrieving bookings", e);
        }
    }

    @Override
    public Optional<List<Booking>> findByResidenceId(Long residenceId) {
        log.info("Finding bookings for residence ID: {}", residenceId);
        String sql = SELECT_ALL_JOIN + " WHERE b.residence_id = ?";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, residenceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
            
            if (bookings.isEmpty()) {
                log.info("No bookings found for residence ID: {}", residenceId);
                return Optional.empty();
            }
            
            log.info("Successfully retrieved {} bookings for residence ID: {}", bookings.size(), residenceId);
            return Optional.of(bookings);

        } catch (SQLException e) {
            log.error("Error finding bookings by residence ID: {}", residenceId, e);
            throw new RuntimeException("Error finding bookings by residence ID", e);
        }
    }

// ==================== UPDATE ====================

    @Override
    public Optional<Booking> update(Booking booking) {
        log.info("Updating booking with ID: {}", booking.getId());
        String sql = "UPDATE bookings SET start_date = ?, end_date = ?, residence_id = ?, user_id = ? WHERE id = ? RETURNING id, start_date, end_date, residence_id, user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, DateConverter.convertTimestampFromLocalDateTime(booking.getStartDate()));
            ps.setTimestamp(2, DateConverter.convertTimestampFromLocalDateTime(booking.getEndDate()));
            ps.setLong(3, booking.getResidenceId());
            ps.setLong(4, booking.getUserId());
            ps.setLong(5, booking.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking updatedBooking = mapResultSetToBooking(rs);
                    log.info("Successfully updated booking with ID: {}", booking.getId());
                    return Optional.of(updatedBooking);
                }
            }

            log.warn("No booking updated with ID: {}", booking.getId());
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error updating booking with ID: {}", booking.getId(), e);
            throw new RuntimeException("Error updating booking", e);
        }
    }

// ==================== DELETE ====================

    @Override
    public boolean deleteById(Long id) {
        log.info("Deleting booking with ID: {}", id);
        String sql = "DELETE FROM bookings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("Successfully deleted booking with ID: {}", id);
                return true;
            }
            
            log.warn("No booking found to delete with ID: {}", id);
            return false;

        } catch (SQLException e) {
            log.error("Error deleting booking with ID: {}", id, e);
            throw new RuntimeException("Error deleting booking", e);
        }
    }

    @Override
    public int deleteAll() {
        log.info("Deleting all bookings");
        String sql = "DELETE FROM bookings";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int deleted = ps.executeUpdate();
            log.info("Successfully deleted {} bookings", deleted);
            return deleted;

        } catch (SQLException e) {
            log.error("Error deleting all bookings", e);
            throw new RuntimeException("Error deleting all bookings", e);
        }
    }

// ==================== UTILITY ====================

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getLong("id"));
        
        booking.setStartDate(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("start_date")));
        booking.setEndDate(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("end_date")));
        
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        booking.setUserId(user.getId());
        
        Residence residence = new Residence();
        residence.setId(rs.getLong("residence_id"));
        residence.setName(rs.getString("residence_name"));
        residence.setAddress(rs.getString("residence_address"));
        booking.setResidenceId(residence.getId());
        
        return booking;
    }

    public boolean existsOverlappingBooking(Long residenceId, LocalDateTime start, LocalDateTime end) {
        log.debug("Checking for overlapping bookings for residence ID: {} between {} and {}", residenceId, start, end);
        String sql = """
            SELECT 1
            FROM bookings
            WHERE residence_id = ?
            AND start_date < ?
            AND end_date > ?
            LIMIT 1
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, residenceId);
            ps.setTimestamp(2, DateConverter.convertTimestampFromLocalDateTime(end));
            ps.setTimestamp(3, DateConverter.convertTimestampFromLocalDateTime(start));

            try (ResultSet rs = ps.executeQuery()) {
                boolean exists = rs.next();
                if (exists) {
                    log.debug("Overlapping booking found for residence ID: {}", residenceId);
                } else {
                    log.debug("No overlapping bookings found for residence ID: {}", residenceId);
                }
                return exists;
            }

        } catch (SQLException e) {
            log.error("Error checking overlapping bookings for residence ID: {}", residenceId, e);
            throw new RuntimeException("Error checking overlapping bookings", e);
        }
    }

}