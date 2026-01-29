package com.giuseppe_tesse.turista.dao.impl;

import com.giuseppe_tesse.turista.model.User;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import com.giuseppe_tesse.turista.util.DateConverter;
import com.giuseppe_tesse.turista.util.PasswordHasher;
import com.giuseppe_tesse.turista.dao.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDAOImpl implements UserDAO {

// ==================== CREATE ====================

    @Override
    public User create(User user) {
        log.info("Creating new user with email: {}", user.getEmail());
        String sql = "INSERT INTO users (first_name, last_name, email, password, address, registration_date) VALUES (?, ?, ?, ?, ?, ?) RETURNING id, registration_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, PasswordHasher.hash(user.getPassword()));
            ps.setString(5, user.getAddress());
            ps.setDate(6, DateConverter.toSqlDate(user.getRegistrationDate()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                    user.setRegistrationDate(DateConverter.date2LocalDate(rs.getDate("registration_date")));
                    log.info("User created successfully - ID: {}, Email: {}", user.getId(), user.getEmail());
                } else {
                    log.error("Creating user failed, no ID obtained");
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            
            return user;

        } catch (SQLException e) {
            log.error("Error creating user with email: {}", user.getEmail(), e);
            throw new RuntimeException("Error creating user", e);
        }
    }

// ==================== READ ====================

    @Override
    public List<User> findAll() {
        log.info("Retrieving all users");
        String sql = "SELECT id, first_name, last_name, email, password, address, registration_date FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            log.info("Successfully retrieved {} users", users.size());
            return users;

        } catch (SQLException e) {
            log.error("Error retrieving all users", e);
            throw new RuntimeException("Error retrieving users", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        log.info("Finding user by ID: {}", id);
        String sql = "SELECT id, first_name, last_name, email, password, address, registration_date FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    log.info("Successfully found user with ID: {}", id);
                    return Optional.of(user);
                }
            }

            log.warn("No user found with ID: {}", id);
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error finding user by ID: {}", id, e);
            throw new RuntimeException("Error finding user by ID: " + id, e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Finding user by email: {}", email);
        String sql = "SELECT id, first_name, last_name, email, password, address, registration_date FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    log.info("Successfully found user with email: {}", email);
                    return Optional.of(user);
                }
            }

            log.warn("No user found with email: {}", email);
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error finding user by email: {}", email, e);
            throw new RuntimeException("Error finding user by email: " + email, e);
        }
    }

// ==================== UPDATE ====================

    @Override
    public Optional<User> update(User user) {
        log.info("Updating user with ID: {}", user.getId());
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, password = ?, address = ?, registration_date = ? WHERE id = ? RETURNING id, first_name, last_name, email, password, address, registration_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, PasswordHasher.hash(user.getPassword()));
            ps.setString(5, user.getAddress());
            ps.setDate(6, DateConverter.toSqlDate(user.getRegistrationDate()));
            ps.setLong(7, user.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User updatedUser = mapResultSetToUser(rs);
                    log.info("Successfully updated user with ID: {}", user.getId());
                    return Optional.of(updatedUser);
                }
            }

            log.warn("No user updated with ID: {}", user.getId());
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error updating user with ID: {}", user.getId(), e);
            throw new RuntimeException("Error updating user", e);
        }
    }

// ==================== DELETE ====================

    @Override
    public boolean deleteById(Long id) {
        log.info("Deleting user with ID: {}", id);
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("Successfully deleted user with ID: {}", id);
                return true;
            }
            
            log.warn("No user deleted with ID: {}", id);
            return false;

        } catch (SQLException e) {
            log.error("Error deleting user with ID: {}", id, e);
            throw new RuntimeException("Error deleting user with ID: " + id, e);
        }
    }

    @Override
    public int deleteAll() {
        log.info("Deleting all users");
        String sql = "DELETE FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int deleted = ps.executeUpdate();
            log.info("Successfully deleted {} users", deleted);
            return deleted;

        } catch (SQLException e) {
            log.error("Error deleting all users", e);
            throw new RuntimeException("Error deleting all users", e);
        }
    }

    @Override
    public boolean deleteByEmail(String email) {
        log.info("Deleting user with email: {}", email);
        String sql = "DELETE FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("Successfully deleted user with email: {}", email);
                return true;
            }
            
            log.warn("No user deleted with email: {}", email);
            return false;

        } catch (SQLException e) {
            log.error("Error deleting user with email: {}", email, e);
            throw new RuntimeException("Error deleting user with email: " + email, e);
        }
    }

// ==================== UTILITY ====================

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setAddress(rs.getString("address"));
        user.setRegistrationDate(DateConverter.date2LocalDate(rs.getDate("registration_date")));
        return user;
    }
}