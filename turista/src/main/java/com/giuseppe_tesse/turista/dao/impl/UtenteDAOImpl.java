package com.giuseppe_tesse.turista.dao.impl;

import com.giuseppe_tesse.turista.model.Utente;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import com.giuseppe_tesse.turista.exception.DuplicateUserException;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import com.giuseppe_tesse.turista.util.DateConverter;
import com.giuseppe_tesse.turista.util.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



import com.giuseppe_tesse.turista.dao.UtenteDAO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UtenteDAOImpl implements UtenteDAO {

    


// ==================== CREATE ====================

    @Override
    public Utente create(Utente utente) {
        String sql = "INSERT INTO utenti (nome,cognome,email, password, indirizzo, data_registrazione) VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, utente.getNome());
            ps.setString(2, utente.getCognome());
            ps.setString(3, utente.getEmail());
            String hashed = PasswordHasher.hash(utente.getPassword());
            ps.setString(4, hashed);
            ps.setString(5, utente.getIndirizzo());
            ps.setDate(6, DateConverter.toSqlDate(utente.getData_registrazione()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    utente.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            log.info("User created with ID: " + utente.getId());
            return utente;

        } catch (DuplicateUserException e) {
            log.error("Duplicate email: " + utente.getEmail(), e);
            throw new DuplicateUserException("Email already exists: " + utente.getEmail());
        } catch (SQLException e) {
            log.error("Error creating user", e);
            throw new RuntimeException("Error creating user", e);
        }
    }

// ==================== READ ====================

    @Override
    public List<Utente> findAll() {
        String sql = "SELECT * FROM utenti";
        List<Utente> utenti = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                utenti.add(mapResultSetToUtente(rs));
            }

            log.info("Retrieved " + utenti.size() + " users.");
            return utenti;

        }catch (SQLException e) {
            log.error("Error retrieving users", e);
            throw new RuntimeException("Error retrieving users", e);
        } 
    }

    @Override
    public Optional<Utente> findById(Long id) {
        String sql = "SELECT * FROM utenti WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUtente(rs));
                }
            } catch (UserNotFoundException e) {
                log.error("User not found with ID: " + id, e);
                throw new UserNotFoundException("User not found with ID: " + id);
            }

        } 
        catch (SQLException e) {
            log.error("Error finding user by ID: " + id, e);
            throw new RuntimeException("Error finding user by ID: " + id, e);
        }
        log.debug("No user found with ID: " + id);
        return Optional.empty();
}

    @Override
    public Optional<Utente> findByEmail(String email) {
        String sql = "SELECT * FROM utenti WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUtente(rs));
                }
            } catch (UserNotFoundException e) {
                log.error("User not found with email: " + email, e);
                throw new UserNotFoundException("User not found with email: " + email);
            }

        } 
        catch (SQLException e) {
            log.error("Error finding user by email: " + email, e);
            throw new RuntimeException("Error finding user by email: " + email, e);
        }
        log.debug("No user found with email: " + email);
        return Optional.empty();
    }


// ==================== UPDATE ====================

    @Override
public Optional<Utente> update(Utente utente) {

    StringBuilder sql = new StringBuilder("UPDATE utenti SET ");
    List<Object> params = new ArrayList<>();

    if (utente.getNome() != null) {
        sql.append("nome = ?, ");
        params.add(utente.getNome());
    }
    if (utente.getCognome() != null) {
        sql.append("cognome = ?, ");
        params.add(utente.getCognome());
    }
    if (utente.getEmail() != null) {
        sql.append("email = ?, ");
        params.add(utente.getEmail());
    }
    if (utente.getPassword() != null) {
        sql.append("password = ?, ");
        params.add(utente.getPassword());
    }
    if (utente.getIndirizzo() != null) {
        sql.append("indirizzo = ?, ");
        params.add(utente.getIndirizzo());
    }
    if (utente.getData_registrazione() != null) {
        sql.append("data_registrazione = ?, ");
        params.add(DateConverter.toSqlDate(utente.getData_registrazione()));
    }

    if (params.isEmpty()) {
        return Optional.of(utente);
    }

    // rimuove ultima virgola
    sql.setLength(sql.length() - 2);
    sql.append(" WHERE id = ?");
    params.add(utente.getId());

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {

        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        int rows = ps.executeUpdate();
        return rows > 0 ? Optional.of(utente) : Optional.empty();

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

// ==================== DELETE ====================


    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM utenti WHERE id = ?";

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
        String sql = "DELETE FROM utenti";

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
        String sql = "DELETE FROM utenti WHERE email = ?";

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

    private Utente mapResultSetToUtente(ResultSet rs) throws SQLException {
        Utente utente = new Utente();
        utente.setId(rs.getLong("id"));
        utente.setNome(rs.getString("nome"));
        utente.setCognome(rs.getString("cognome"));
        utente.setEmail(rs.getString("email"));
        utente.setPassword(rs.getString("password"));
        utente.setIndirizzo(rs.getString("indirizzo"));
        utente.setData_registrazione(DateConverter.date2LocalDate(rs.getDate("data_registrazione")));
        return utente;
    }

}
