package com.giuseppe_tesse.turista.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.dao.BookingDAO;
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
        String sql = "INSERT INTO bookings (start_date, end_date, residence_id, user_id) VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, DateConverter.convertTimestampFromLocalDateTime(booking.getStartDate()));
            ps.setTimestamp(2, DateConverter.convertTimestampFromLocalDateTime(booking.getEndDate()));
            ps.setLong(3, booking.getResidence().getId());
            ps.setLong(4, booking.getUser().getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    booking.setId(rs.getLong("id"));
                } else {
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            }

            log.info("Booking created with ID: {}", booking.getId());
            return booking;

        } catch (SQLException e) {
            log.error("Error creating booking for user ID: {}", booking.getUser().getId(), e);
            throw new RuntimeException("Error creating booking", e);
        }
    }

// ==================== READ ====================

    @Override
    public Optional<Booking> findById(Long id) {
        String sql = SELECT_ALL_JOIN + " WHERE b.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBooking(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Errore findById booking: {}", id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Booking> findAll() {
        String sql = "SELECT id, start_date, end_date, residence_id, user_id FROM bookings";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            log.info("Retrieved {} bookings", bookings.size());
            return bookings;

        } catch (SQLException e) {
            log.error("Error retrieving bookings", e);
            throw new RuntimeException("Error retrieving bookings", e);
        }
    }

    @Override
    public Optional<List<Booking>> findByResidenceId(Long residenceId) {
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
            return bookings.isEmpty() ? Optional.empty() : Optional.of(bookings);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

// ==================== UPDATE ====================

    @Override
    public Optional<Booking> update(Booking booking) {
        String sql = "UPDATE bookings SET start_date = ?, end_date = ?, residence_id = ?, user_id = ? WHERE id = ? RETURNING id, start_date, end_date, residence_id, user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, DateConverter.convertTimestampFromLocalDateTime(booking.getStartDate()));
            ps.setTimestamp(2, DateConverter.convertTimestampFromLocalDateTime(booking.getEndDate()));
            ps.setLong(3, booking.getResidence().getId());
            ps.setLong(4, booking.getUser().getId());
            ps.setLong(5, booking.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBooking(rs));
                }
            }

            log.debug("No booking updated with ID: {}", booking.getId());
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error updating booking ID: {}", booking.getId(), e);
            throw new RuntimeException("Error updating booking", e);
        }
    }

// ==================== DELETE ====================

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM bookings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("Booking deleted with ID: {}", id);
                return true;
            }
            
            log.debug("No booking found to delete with ID: {}", id);
            return false;

        } catch (SQLException e) {
            log.error("Error deleting booking with ID: {}", id, e);
            throw new RuntimeException("Error deleting booking", e);
        }
    }

    @Override
    public int deleteAll() {
        String sql = "DELETE FROM bookings";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int deleted = ps.executeUpdate();
            log.info("Deleted {} bookings", deleted);
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
        
        // Conversione date (da SQL DATE a LocalDateTime)
        booking.setStartDate(rs.getDate("start_date").toLocalDate().atStartOfDay());
        booking.setEndDate(rs.getDate("end_date").toLocalDate().atStartOfDay());
        
        // Mapping dell'oggetto USER (Senza chiamare UserDAO!)
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        booking.setUser(user);
        
        // Mapping dell'oggetto RESIDENCE (Senza chiamare ResidenceDAO!)
        Residence residence = new Residence();
        residence.setId(rs.getLong("residence_id"));
        residence.setName(rs.getString("residence_name"));
        residence.setAddress(rs.getString("residence_address"));
        booking.setResidence(residence);
        
        return booking;
    }
}