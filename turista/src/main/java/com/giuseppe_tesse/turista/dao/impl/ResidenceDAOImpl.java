package com.giuseppe_tesse.turista.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.dao.ResidenceDAO;
import com.giuseppe_tesse.turista.model.Residence;
import com.giuseppe_tesse.turista.model.Host;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import com.giuseppe_tesse.turista.util.DateConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResidenceDAOImpl implements ResidenceDAO {

// ==================== CREATE ====================

    @Override
    public Residence create(Residence residence) {
        String sql = "INSERT INTO residences (name, address, number_of_rooms, number_of_beds, floor, price, availability_start, availability_end, host_id) VALUES (?,?,?,?,?,?,?,?,?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, residence.getName());
            ps.setString(2, residence.getAddress());
            ps.setInt(3, residence.getNumber_of_rooms());
            ps.setInt(4, residence.getGuest_capacity());
            ps.setInt(5, residence.getFloor());
            ps.setDouble(6, residence.getPrice_per_night());
            ps.setDate(7, DateConverter.toSqlDate(residence.getAvailable_from()));
            ps.setDate(8, DateConverter.toSqlDate(residence.getAvailable_to()));
            ps.setLong(9, residence.getHostId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    residence.setId(rs.getLong("id"));
                } else {
                    throw new SQLException("Creating residence failed, no ID obtained.");
                }
            }

            log.info("Residence created with ID: {}", residence.getId());
            return residence;

        } catch (SQLException e) {
            log.error("Error creating residence: {}", residence.getName(), e);
            throw new RuntimeException("Error creating residence", e);
        }
    }

// ==================== READ ====================

    @Override
    public List<Residence> findAll() {
        String sql = "SELECT id, name, address, number_of_rooms, number_of_beds, floor, price, availability_start, availability_end, host_id FROM residences";
        List<Residence> residences = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                residences.add(mapResultSetToResidence(rs));
            }

            log.info("Retrieved {} residences", residences.size());
            return residences;

        } catch (SQLException e) {
            log.error("Error retrieving residences", e);
            throw new RuntimeException("Error retrieving residences", e);
        }
    }

    @Override
    public List<Residence> findByHostId(Long hostId) {
        String sql = "SELECT id, name, address, number_of_rooms, number_of_beds, floor, price, availability_start, availability_end, host_id FROM residences WHERE host_id = ?";
        List<Residence> residences = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, hostId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    residences.add(mapResultSetToResidence(rs));
                }
            }
            
            log.info("Retrieved {} residences for host ID: {}", residences.size(), hostId);
            return residences;

        } catch (SQLException e) {
            log.error("Error finding residences by host ID: {}", hostId, e);
            throw new RuntimeException("Error finding residences by host ID", e);
        }
    }

    @Override
    public Optional<Residence> findById(Long id) {
        String sql = "SELECT id, name, address, number_of_rooms, number_of_beds, floor, price, availability_start, availability_end, host_id FROM residences WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToResidence(rs));
                }
            }
            
            log.debug("No residence found with ID: {}", id);
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("Error finding residence by ID: {}", id, e);
            throw new RuntimeException("Error finding residence by ID", e);
        }
    }

    @Override
    public Optional<Residence> findByAddressAndFloor(String address, int floor) {
        String sql = "SELECT id, name, address, number_of_rooms, number_of_beds, floor, price, availability_start, availability_end, host_id FROM residences WHERE address = ? AND floor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, address);
            ps.setInt(2, floor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToResidence(rs));
                }
            }
            
            log.debug("No residence found at address: {}, floor: {}", address, floor);
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("Error finding residence by address and floor", e);
            throw new RuntimeException(e);
        }
    }

// ==================== UPDATE ====================

    @Override
    public Optional<Residence> update(Residence residence) {
        String sql = "UPDATE residences SET name=?, address=?, number_of_rooms=?, number_of_beds=?, floor=?, price=?, availability_start=?, availability_end=?, host_id=? WHERE id=? RETURNING id, name, address, number_of_rooms, number_of_beds, floor, price, availability_start, availability_end, host_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, residence.getName());
            ps.setString(2, residence.getAddress());
            ps.setInt(3, residence.getNumber_of_rooms());
            ps.setInt(4, residence.getGuest_capacity());
            ps.setInt(5, residence.getFloor());
            ps.setDouble(6, residence.getPrice_per_night());
            ps.setDate(7, DateConverter.toSqlDate(residence.getAvailable_from()));
            ps.setDate(8, DateConverter.toSqlDate(residence.getAvailable_to()));
            ps.setLong(9, residence.getHostId());
            ps.setLong(10, residence.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToResidence(rs));
                }
            }

            log.debug("No residence updated with ID: {}", residence.getId());
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error updating residence ID: {}", residence.getId(), e);
            throw new RuntimeException(e);
        }
    }

// ==================== DELETE ====================

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM residences WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                log.info("Residence deleted with ID: {}", id);
                return true;
            }
            
            log.debug("No residence deleted with ID: {}", id);
            return false;
            
        } catch (SQLException e) {
            log.error("Error deleting residence with ID: {}", id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteAll() {
        String sql = "DELETE FROM residences";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int deleted = ps.executeUpdate();
            log.info("Deleted {} residences", deleted);
            return deleted;
            
        } catch (SQLException e) {
            log.error("Error deleting all residences", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteByHostId(Long hostId) {
        String sql = "DELETE FROM residences WHERE host_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, hostId);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                log.info("Residences deleted for host ID: {}", hostId);
                return true;
            }
            
            log.debug("No residences deleted for host ID: {}", hostId);
            return false;
            
        } catch (SQLException e) {
            log.error("Error deleting residences by host ID: {}", hostId, e);
            throw new RuntimeException(e);
        }
    }

// ==================== UTILITY ====================

    private Residence mapResultSetToResidence(ResultSet rs) throws SQLException {
        Residence residence = new Residence();
        residence.setId(rs.getLong("id"));
        residence.setName(rs.getString("name"));
        residence.setAddress(rs.getString("address"));
        residence.setNumber_of_rooms(rs.getInt("number_of_rooms"));
        residence.setGuest_capacity(rs.getInt("number_of_beds"));
        residence.setFloor(rs.getInt("floor"));
        residence.setPrice_per_night(rs.getDouble("price"));
        residence.setAvailable_from(DateConverter.date2LocalDate(rs.getDate("availability_start")));
        residence.setAvailable_to(DateConverter.date2LocalDate(rs.getDate("availability_end")));
        Host host = new Host();
        host.setId(rs.getLong("host_id"));
        Long hostId=host.getId();
        residence.setHostId(hostId);
        
        return residence;
    }
}