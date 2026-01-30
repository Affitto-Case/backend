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

    private final String SELECT_WITH_HOST = 
        "SELECT r.*, u.first_name, u.last_name, u.email, h.host_code " +
        "FROM residences r " +
        "JOIN hosts h ON r.host_id = h.user_id " +
        "JOIN users u ON h.user_id = u.id ";

// ==================== CREATE ====================

    @Override
    public Residence create(Residence residence) {
        log.info("Creating new residence: {}", residence.getName());
        String sql = "INSERT INTO residences (name, address, number_of_rooms, number_of_beds, floor, price, availability_start, availability_end, host_id) VALUES (?,?,?,?,?,?,?,?,?) RETURNING id,host_id";
        
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
            ps.setLong(9, residence.getHost().getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    residence.setId(rs.getLong("id"));
                    log.info("Residence created successfully with ID: {}", residence.getId());
                    
                } else {
                    log.error("Creating residence failed, no ID obtained");
                    throw new SQLException("Creating residence failed, no ID obtained.");
                }
            }

            return residence;

        } catch (SQLException e) {
            log.error("Error creating residence: {}", residence.getName(), e);
            throw new RuntimeException("Error creating residence", e);
        }
    }

// ==================== READ ====================

    @Override
    public List<Residence> findAll() {
        log.info("Retrieving all residences");
        List<Residence> residences = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_WITH_HOST);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                residences.add(mapResultSetToResidence(rs));
            }

            log.info("Successfully retrieved {} residences", residences.size());
            return residences;

        } catch (SQLException e) {
            log.error("Error retrieving all residences", e);
            throw new RuntimeException("Error retrieving residences", e);
        }
    }

    @Override
    public List<Residence> findByHostId(Long hostId) {
        log.info("Retrieving residences for host ID: {}", hostId);
        String sql = SELECT_WITH_HOST + " WHERE r.host_id = ?";
        List<Residence> residences = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, hostId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    residences.add(mapResultSetToResidence(rs));
                }
            }
            
            log.info("Successfully retrieved {} residences for host ID: {}", residences.size(), hostId);
            return residences;

        } catch (SQLException e) {
            log.error("Error finding residences by host ID: {}", hostId, e);
            throw new RuntimeException("Error finding residences by host ID", e);
        }
    }

    @Override
    public Optional<Residence> findById(Long id) {
        log.info("Finding residence by ID: {}", id);
        String sql = SELECT_WITH_HOST + " WHERE r.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Residence residence = mapResultSetToResidence(rs);
                    log.info("Successfully found residence with ID: {}", id);
                    return Optional.of(residence);
                }
            }
            
            log.warn("No residence found with ID: {}", id);
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("Error finding residence by ID: {}", id, e);
            throw new RuntimeException("Error finding residence by ID", e);
        }
    }

    @Override
    public Optional<Residence> findByAddressAndFloor(String address, int floor) {
        log.info("Finding residence by address: {} and floor: {}", address, floor);
        String sql = "SELECT id, name, address, number_of_rooms, number_of_beds, floor, price, availability_start, availability_end, host_id FROM residences WHERE address = ? AND floor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, address);
            ps.setInt(2, floor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Residence residence = mapResultSetToResidence(rs);
                    log.info("Successfully found residence at address: {}, floor: {}", address, floor);
                    return Optional.of(residence);
                }
            }
            
            log.warn("No residence found at address: {}, floor: {}", address, floor);
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("Error finding residence by address: {} and floor: {}", address, floor, e);
            throw new RuntimeException("Error finding residence by address and floor", e);
        }
    }

    @Override
    public List<Residence> findByHostCode(String code) {
        log.info("Finding residences by host code: {}", code);
        String sql = "SELECT r.id, r.name, r.address, r.number_of_rooms, r.number_of_beds, r.floor, r.price, r.availability_start, r.availability_end, r.host_id " +
                     "FROM residences r " +
                     "JOIN hosts h ON r.host_id = h.user_id " +
                     "WHERE h.host_code = ?";
        List<Residence> residences = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    residences.add(mapResultSetToResidence(rs));
                }
            }
            
            log.info("Successfully retrieved {} residences for host code: {}", residences.size(), code);
            return residences;

        } catch (SQLException e) {
            log.error("Error finding residences by host code: {}", code, e);
            throw new RuntimeException("Error finding residences by host code", e);
        }
    }

// ==================== UPDATE ====================

    @Override
    public Optional<Residence> update(Residence residence) {
        log.info("Updating residence with ID: {}", residence.getId());
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
            ps.setLong(9, residence.getHost().getId());
            ps.setLong(10, residence.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Residence updatedResidence = mapResultSetToResidence(rs);
                    log.info("Successfully updated residence with ID: {}", residence.getId());
                    return Optional.of(updatedResidence);
                }
            }

            log.warn("No residence updated with ID: {}", residence.getId());
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error updating residence with ID: {}", residence.getId(), e);
            throw new RuntimeException("Error updating residence", e);
        }
    }

// ==================== DELETE ====================

    @Override
    public boolean deleteById(Long id) {
        log.info("Deleting residence with ID: {}", id);
        String sql = "DELETE FROM residences WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                log.info("Successfully deleted residence with ID: {}", id);
                return true;
            }
            
            log.warn("No residence deleted with ID: {}", id);
            return false;
            
        } catch (SQLException e) {
            log.error("Error deleting residence with ID: {}", id, e);
            throw new RuntimeException("Error deleting residence", e);
        }
    }

    @Override
    public int deleteAll() {
        log.info("Deleting all residences");
        String sql = "DELETE FROM residences";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int deleted = ps.executeUpdate();
            log.info("Successfully deleted {} residences", deleted);
            return deleted;
            
        } catch (SQLException e) {
            log.error("Error deleting all residences", e);
            throw new RuntimeException("Error deleting all residences", e);
        }
    }

    @Override
    public boolean deleteByHostId(Long hostId) {
        log.info("Deleting residences for host ID: {}", hostId);
        String sql = "DELETE FROM residences WHERE host_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, hostId);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                log.info("Successfully deleted {} residences for host ID: {}", rowsAffected, hostId);
                return true;
            }
            
            log.warn("No residences deleted for host ID: {}", hostId);
            return false;
            
        } catch (SQLException e) {
            log.error("Error deleting residences by host ID: {}", hostId, e);
            throw new RuntimeException("Error deleting residences by host ID", e);
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
        host.setFirstName(rs.getString("first_name"));
        host.setLastName(rs.getString("last_name"));
        host.setEmail(rs.getString("email"));
        host.setHost_code(rs.getString("host_code"));
        
        residence.setHost(host);
        
        return residence;
    }
}