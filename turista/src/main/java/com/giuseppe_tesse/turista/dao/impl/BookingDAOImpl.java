package com.giuseppe_tesse.turista.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.dao.BookingDAO;
import com.giuseppe_tesse.turista.exception.ResidenceNotFoundException;
import com.giuseppe_tesse.turista.exception.DuplicateBookingException;
import com.giuseppe_tesse.turista.exception.DuplicateUserException;
import com.giuseppe_tesse.turista.exception.BookingNotFoundException;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import com.giuseppe_tesse.turista.util.DateConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingDAOImpl implements BookingDAO {
    private final UserDAOImpl userDAO;
    private final ResidenceDAOImpl residenceDAO;

    public BookingDAOImpl() {
        this.userDAO = new UserDAOImpl();
        this.residenceDAO = new ResidenceDAOImpl();
    }

// ==================== CREATE ====================

    @Override
    public Booking create(Booking booking) {
        String sql = "INSERT INTO bookings (start_date, end_date, residence_id, user_id) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, DateConverter.convertTimestampFromLocalDateTime(booking.getStartDate()));
            ps.setTimestamp(2, DateConverter.convertTimestampFromLocalDateTime(booking.getEndDate()));
            ps.setLong(3, booking.getResidence().getId());
            ps.setLong(4, booking.getUser().getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    booking.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            }

            log.info("Booking created with ID: " + booking.getId());
            return booking;

        } catch (DuplicateBookingException e) {
            log.error("Duplicate booking: " + e);
            throw new DuplicateUserException("Booking already exists");
        } catch (SQLException e) {
            log.error("Error creating booking", e);
            throw new RuntimeException("Error creating booking", e);
        }
    }

// ==================== READ ====================

    @Override
    public Optional<Booking> findById(Long id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBooking(rs));
                }
            } catch (BookingNotFoundException e) {
                log.error("Booking not found with ID: " + id, e);
                throw new BookingNotFoundException("Booking not found with ID: " + id);
            }

        } catch (SQLException e) {
            log.error("Error finding booking by ID: " + id, e);
            throw new RuntimeException("Error finding booking by ID: " + id, e);
        }
        log.debug("No booking found with ID: " + id);
        return Optional.empty();
    }

    @Override
    public List<Booking> findAll() {
        String sql = "SELECT * FROM bookings";
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            log.info("Retrieved " + bookings.size() + " bookings.");
            return bookings;

        } catch (SQLException e) {
            log.error("Error retrieving bookings", e);
            throw new RuntimeException("Error retrieving bookings", e);
        }
    }

    @Override
    public Optional<List<Booking>> findByResidenceId(Long residenceId) {
        String sql = "SELECT * FROM bookings JOIN residences on bookings.residence_id = residences.id WHERE residences.id = ?";
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, residenceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            } catch (BookingNotFoundException e) {
                log.error("Booking not found with residence ID: " + residenceId, e);
                throw new BookingNotFoundException("Booking not found with residence ID: " + residenceId);
            }

        } catch (SQLException e) {
            log.error("Error finding booking by residence ID: " + residenceId, e);
            throw new RuntimeException("Error finding booking by ID: " + residenceId, e);
        }
        
        return bookings.isEmpty() ? Optional.empty() : Optional.of(bookings);
    }

// ==================== UPDATE ====================

    @Override
    public Optional<Booking> update(Booking booking) {
        StringBuilder sql = new StringBuilder("UPDATE bookings SET ");
        List<Object> params = new ArrayList<>();

        if (booking.getStartDate() != null) {
            sql.append("start_date = ?, ");
            params.add(booking.getStartDate());
        }

        if (booking.getEndDate() != null) {
            sql.append("end_date = ?, ");
            params.add(booking.getEndDate());
        }

        if (params.isEmpty()) {
            return Optional.of(booking);
        }
        
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(booking.getId());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            int rows = ps.executeUpdate();
            return rows > 0 ? Optional.of(booking) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                log.info("Booking deleted with ID: " + id);
                return true;
            } else {
                log.debug("No booking found to delete with ID: " + id);
                return false;
            }

        } catch (SQLException e) {
            log.error("Error deleting booking with ID: " + id, e);
            throw new RuntimeException("Error deleting booking with ID: " + id, e);
        }
    }

    @Override
    public int deleteAll() {
        String sql = "DELETE FROM bookings";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int rowsAffected = ps.executeUpdate();
            log.info("Deleted " + rowsAffected + " bookings.");
            return rowsAffected;

        } catch (SQLException e) {
            log.error("Error deleting all bookings", e);
            throw new RuntimeException("Error deleting all bookings", e);
        }
    }

// ==================== UTILITY ====================

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        long userId = rs.getLong("user_id");
        long residenceId = rs.getLong("residence_id");
        Booking booking = new Booking();
        booking.setId(rs.getLong("id"));
        booking.setStartDate(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("start_date")));
        booking.setEndDate(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("end_date")));
        
        booking.setUser(userDAO.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found")));
        
        booking.setResidence(residenceDAO.findById(residenceId)
            .orElseThrow(() -> new ResidenceNotFoundException(residenceId)));
        
        return booking;
    }
}