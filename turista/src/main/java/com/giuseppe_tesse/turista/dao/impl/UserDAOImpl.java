package com.giuseppe_tesse.turista.dao.impl;

import com.giuseppe_tesse.turista.model.User;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import com.giuseppe_tesse.turista.exception.DuplicateUserException;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import com.giuseppe_tesse.turista.util.DateConverter;
import com.giuseppe_tesse.turista.util.PasswordHasher;
import com.giuseppe_tesse.turista.dao.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDAOImpl implements UserDAO {

// ==================== CREATE ====================

   @Override
public User create(User user) {
    String sql = "INSERT INTO users (first_name, last_name, email, password, address, registration_date) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setString(1, user.getFirstName());
        ps.setString(2, user.getLastName());
        ps.setString(3, user.getEmail());
        String hashed = PasswordHasher.hash(user.getPassword());
        ps.setString(4, hashed);
        ps.setString(5, user.getAddress());
        ps.setDate(6, DateConverter.toSqlDate(user.getRegistrationDate()));

        int affectedRows = ps.executeUpdate();
        
        if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
        }

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                user.setId(rs.getLong(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }

        log.info("User created with ID: " + user.getId());
        return user;

    } catch (SQLException e) {
        // Controlla se è una violazione di unique constraint (email duplicata)
        if (e.getMessage().contains("duplicate") || e.getMessage().contains("unique") 
            || e.getSQLState().equals("23505")) { // 23505 = unique violation in PostgreSQL
            log.error("Duplicate email: " + user.getEmail(), e);
            throw new DuplicateUserException("Email already exists: " + user.getEmail());
        }
        log.error("Error creating user", e);
        throw new RuntimeException("Error creating user", e);
    }
}
// ==================== READ ====================

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            log.info("Retrieved " + users.size() + " users.");
            return users;

        } catch (SQLException e) {
            log.error("Error retrieving users", e);
            throw new RuntimeException("Error retrieving users", e);
        }
    }

    @Override
public Optional<User> findById(Long id) {
    String sql = "SELECT * FROM users WHERE id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setLong(1, id);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        }

        log.debug("No user found with ID: " + id);
        return Optional.empty();

    } catch (SQLException e) {
        log.error("Error finding user by ID: " + id, e);
        throw new UserNotFoundException("Error finding user by ID: " + id + ": "+e);
    }
}


    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            } catch (UserNotFoundException e) {
                log.error("User not found with email: " + email, e);
                throw new UserNotFoundException("User not found with email: " + email);
            }

        } catch (SQLException e) {
            log.error("Error finding user by email: " + email, e);
            throw new RuntimeException("Error finding user by email: " + email, e);
        }
        log.debug("No user found with email: " + email);
        return Optional.empty();
    }

// ==================== UPDATE ====================

    @Override
    public Optional<User> update(User user) {

        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        List<Object> params = new ArrayList<>();

        if (user.getFirstName() != null) {
            sql.append("first_name = ?, ");
            params.add(user.getFirstName());
        }
        if (user.getLastName() != null) {
            sql.append("last_name = ?, ");
            params.add(user.getLastName());
        }
        if (user.getEmail() != null) {
            sql.append("email = ?, ");
            params.add(user.getEmail());
        }
       if (user.getPassword() != null) {
        sql.append("password = ?, ");
        params.add(PasswordHasher.hash(user.getPassword())); 
    }
        if (user.getAddress() != null) {
            sql.append("address = ?, ");
            params.add(user.getAddress());
        }
        if (user.getRegistrationDate() != null) {
            sql.append("registration_date = ?, ");
            params.add(DateConverter.toSqlDate(user.getRegistrationDate()));
        }

        if (params.isEmpty()) {
            return Optional.of(user);
        }

        // rimuove ultima virgola
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(user.getId());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            int rows = ps.executeUpdate();
            return rows > 0 ? Optional.of(user) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

// ==================== DELETE ====================

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("User deleted with ID: " + id);
                return true;
            } else {
                log.debug("No user found to delete with ID: " + id);
                return false;
            }

        } catch (SQLException e) {
            log.error("Error deleting user with ID: " + id, e);
            throw new RuntimeException("Error deleting user with ID: " + id, e);
        }
    }

    @Override
    public int deleteAll() {
        String sql = "DELETE FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int rowsAffected = ps.executeUpdate();
            log.info("Deleted " + rowsAffected + " users.");
            return rowsAffected;

        } catch (SQLException e) {
            log.error("Error deleting all users", e);
            throw new RuntimeException("Error deleting all users", e);
        }
    }

    @Override
    public boolean deleteByEmail(String email) {
        String sql = "DELETE FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("User deleted with email: " + email);
                return true;
            } else {
                log.debug("No user found to delete with email: " + email);
                return false;
            }

        } catch (SQLException e) {
            log.error("Error deleting user with email: " + email, e);
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