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

    @Override
    public Host create(Host host) {
        String sql = "INSERT INTO hosts (user_id, host_code, total_bookings, registration_date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, host.getId());
            ps.setString(2, host.getHost_code());
            ps.setInt(3, host.getTotal_bookings());
            ps.setDate(4, DateConverter.toSqlDate(host.getRegistrationDate()));

            ps.executeUpdate();
            log.info("Host details created for User ID: {}", host.getId());
            return host;
        } catch (SQLException e) {
            log.error("Error creating host", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Host> findAll() {
        String sql = "SELECT u.*, h.host_code, h.total_bookings FROM users u JOIN hosts h ON u.id = h.user_id";
        List<Host> hosts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                hosts.add(mapResultSetToHost(rs));
            }
            return hosts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Host> findById(Long id) {
        String sql = "SELECT u.*, h.host_code, h.total_bookings FROM users u JOIN hosts h ON u.id = h.user_id WHERE u.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToHost(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Host> findByHostCode(String hostCode) {
        String sql = "SELECT u.*, h.host_code, h.total_bookings FROM users u JOIN hosts h ON u.id = h.user_id WHERE h.host_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hostCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToHost(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Host> update(Host host) {
        String sql = "UPDATE hosts SET host_code = ?, total_bookings = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, host.getHost_code());
            ps.setInt(2, host.getTotal_bookings());
            ps.setLong(3, host.getId());
            return ps.executeUpdate() > 0 ? Optional.of(host) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM hosts WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Host mapResultSetToHost(ResultSet rs) throws SQLException {
        Host host = new Host();
        host.setId(rs.getLong("id"));
        host.setFirstName(rs.getString("first_name"));
        host.setLastName(rs.getString("last_name"));
        host.setEmail(rs.getString("email"));
        host.setAddress(rs.getString("address"));
        host.setRegistrationDate(DateConverter.date2LocalDate(rs.getDate("registration_date")));
        host.setHost_code(rs.getString("host_code"));
        host.setTotal_bookings(rs.getInt("total_bookings"));
        return host;
    }
}