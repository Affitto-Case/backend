package com.giuseppe_tesse.turista.dao.impl;

import com.giuseppe_tesse.turista.dao.HostDAO;
import com.giuseppe_tesse.turista.model.Host;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import com.giuseppe_tesse.turista.util.DateConverter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class HostDAOImpl implements HostDAO {

    private final String SELECT_HOST_JOIN = 
        "SELECT u.id AS u_id, u.first_name, u.last_name, u.email, u.address, u.registration_date AS u_reg, " +
        "h.host_code, h.total_bookings, h.registration_date AS h_reg " +
        "FROM users u " +
        "JOIN hosts h ON u.id = h.user_id";

// ==================== CREATE ====================

    @Override
    public Host create(Host host) {
        String sql = "INSERT INTO hosts (user_id, host_code, total_bookings, registration_date) VALUES (?, ?, ?, ?) RETURNING user_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, host.getId());
            ps.setString(2, host.getHost_code());
            ps.setInt(3, host.getTotal_bookings());
            ps.setDate(4, DateConverter.toSqlDate(host.getRegistrationDate()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    host.setId(rs.getLong("user_id"));
                } else {
                    throw new SQLException("Creating host failed, no ID obtained.");
                }
            }

            log.info("Host details created for User ID: {}", host.getId());
            return host;
            
        } catch (SQLException e) {
            log.error("Error creating host for user ID: {}", host.getId(), e);
            throw new RuntimeException("Error creating host", e);
        }
    }

// ==================== READ ====================

    @Override
    public List<Host> findAll() {
        String sql = "SELECT u.id, u.first_name, u.last_name, u.email, u.address, u.registration_date, h.host_code, h.total_bookings FROM users u JOIN hosts h ON u.id = h.user_id";
        List<Host> hosts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                hosts.add(mapResultSetToHost(rs));
            }
            
            log.info("Retrieved {} hosts", hosts.size());
            return hosts;
            
        } catch (SQLException e) {
            log.error("Error retrieving hosts", e);
            throw new RuntimeException("Error retrieving hosts", e);
        }
    }

    @Override
    public Optional<Host> findById(Long id) {
        String sql = SELECT_HOST_JOIN + " WHERE u.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHost(rs));
                }
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("Errore findById host: {}", id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Host> findByHostCode(String hostCode) {
        String sql = SELECT_HOST_JOIN + " WHERE h.host_code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hostCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHost(rs));
                }
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("Errore ricerca host per codice: {}", hostCode, e);
            throw new RuntimeException(e);
        }
    }

// ==================== UPDATE ====================

    @Override
    public Optional<Host> update(Host host) {
        String sql = "UPDATE hosts SET host_code = ?, total_bookings = ? WHERE user_id = ? RETURNING user_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, host.getHost_code());
            ps.setInt(2, host.getTotal_bookings());
            ps.setLong(3, host.getId());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(host);
                }
            }
            
            log.debug("No host updated with ID: {}", host.getId());
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("Error updating host ID: {}", host.getId(), e);
            throw new RuntimeException("Error updating host", e);
        }
    }

// ==================== DELETE ====================

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM hosts WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                log.info("Host deleted with ID: {}", id);
                return true;
            }
            
            log.debug("No host deleted with ID: {}", id);
            return false;
            
        } catch (SQLException e) {
            log.error("Error deleting host with ID: {}", id, e);
            throw new RuntimeException("Error deleting host", e);
        }
    }

// ==================== UTILITY ====================

    private Host mapResultSetToHost(ResultSet rs) throws SQLException {
        Host host = new Host();
        host.setId(rs.getLong("u_id")); 
        host.setFirstName(rs.getString("first_name"));
        host.setLastName(rs.getString("last_name"));
        host.setEmail(rs.getString("email"));
        host.setAddress(rs.getString("address"));
        host.setRegistrationDate(DateConverter.date2LocalDate(rs.getDate("u_reg")));
        host.setHost_code(rs.getString("host_code"));
        host.setTotal_bookings(rs.getInt("total_bookings"));
        
        return host;
    }
}