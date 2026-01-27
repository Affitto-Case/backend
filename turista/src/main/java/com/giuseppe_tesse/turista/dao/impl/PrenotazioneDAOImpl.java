package com.giuseppe_tesse.turista.dao.impl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.dao.AbitazioneDAO;
import com.giuseppe_tesse.turista.dao.PrenotazioneDAO;
import com.giuseppe_tesse.turista.dao.UtenteDAO;
import com.giuseppe_tesse.turista.exception.AbitazioneNotFoundException;
import com.giuseppe_tesse.turista.exception.DuplicatePrenotazioneException;
import com.giuseppe_tesse.turista.exception.DuplicateUserException;
import com.giuseppe_tesse.turista.exception.PrenotazioneNotFoundException;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import com.giuseppe_tesse.turista.model.Abitazione;
import com.giuseppe_tesse.turista.model.Prenotazione;
import com.giuseppe_tesse.turista.model.Utente;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import com.giuseppe_tesse.turista.util.DateConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrenotazioneDAOImpl implements PrenotazioneDAO{
    private UtenteDAOImpl utenteDAO;
    private AbitazioneDAOImpl abitazioneDAO;

    public PrenotazioneDAOImpl() {
        this.utenteDAO = new UtenteDAOImpl();
        this.abitazioneDAO = new AbitazioneDAOImpl();
    }

// ==================== CREATE ====================

    @Override
    public Prenotazione create(Prenotazione prenotazione){
        String sql = "INSERT INTO prenotazioni (data_inizio, data_fine, abitazione_id, utente_id) VALUES (?,?,?,?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setTimestamp(1, DateConverter.convertTimestampFromLocalDateTime(prenotazione.getData_inizio()));
                ps.setTimestamp(2, DateConverter.convertTimestampFromLocalDateTime(prenotazione.getData_fine()));
                ps.setLong(3,prenotazione.getAbitazione().getId());
                ps.setLong(4,prenotazione.getUtente().getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    prenotazione.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating prenotazione failed, no ID obtained.");
                }
            }

            log.info("Prenotazione created with ID: " + prenotazione.getId());
            return prenotazione;

            }catch(DuplicatePrenotazioneException e){
                log.error("Duplicate prenotazione: " + e);
            throw new DuplicateUserException("Prenotazione already exists");
            } catch (SQLException e) {
            log.error("Error creating prenotazione", e);
            throw new RuntimeException("Error creating prenotazione", e);
        }
    }

// ==================== READ ====================

    @Override
    public Optional<Prenotazione> findById(Long id) {
        String sql = "SELECT * FROM prenotazioni WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPrenotazione(rs));
                }
            } catch (PrenotazioneNotFoundException e) {
                log.error("Prenotazione not found with ID: " + id, e);
                throw new PrenotazioneNotFoundException("Prenotazione not found with ID: " + id);
            }

        } 
        catch (SQLException e) {
            log.error("Error finding prenotazione by ID: " + id, e);
            throw new RuntimeException("Error finding prenotazione by ID: " + id, e);
        }
        log.debug("No prenotazione found with ID: " + id);
        return Optional.empty();
       
    }

    @Override
    public List<Prenotazione> findAll() {
        String sql= "SELECT * FROM prenotazioni";
        List<Prenotazione> prenotazioni = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prenotazioni.add(mapResultSetToPrenotazione(rs));
            }

            log.info("Retrieved " + prenotazioni.size() + " prenotazioni.");
            return prenotazioni;

        }catch (SQLException e) {
            log.error("Error retrieving prenotazioni", e);
            throw new RuntimeException("Error retrieving prenotazioni", e);
        } 
    
    }

        @Override
    public Optional<List<Prenotazione>> findByAbitazioneId(Long abitazioneId) {
        String sql = "SELECT * FROM prenotazioni JOIN abitazioni on prenotazioni.abitazione_id = abitazioni.id  WHERE id = ?";
        List<Prenotazione> prenotazioni = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, abitazioneId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    prenotazioni.add(mapResultSetToPrenotazione(rs));
                }
            } catch (PrenotazioneNotFoundException e) {
                log.error("Prenotazione not found with abitazione ID: " + abitazioneId, e);
                throw new PrenotazioneNotFoundException("Prenotazione not found with abitazione ID: " + abitazioneId);
            }

        } 
        catch (SQLException e) {
            log.error("Error finding prenotazione by abitazione ID: " + abitazioneId, e);
            throw new RuntimeException("Error finding prenotazione by ID: " + abitazioneId, e);
        }
        log.debug("No prenotazione found with abitazione ID: " + abitazioneId);
        return Optional.empty();
    }

// ==================== UPDATE ====================

    @Override
    public Optional<Prenotazione> update(Prenotazione prenotazione) {
        StringBuilder sql = new StringBuilder("UPDATE prenotazioni SET ");
        List<Object> params = new ArrayList<>();

        if (prenotazione.getData_inizio() != null){
            sql.append("data_inizio = ?, ");
            params.add(prenotazione.getData_inizio());

        }

        if (prenotazione.getData_fine() != null){
            sql.append("data_fine = ?, ");
            params.add(prenotazione.getData_fine());

        }

        if (params.isEmpty()) {
        return Optional.of(prenotazione);
        }
        // rimuove ultima virgola
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(prenotazione.getId());

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {

        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        int rows = ps.executeUpdate();
        return rows > 0 ? Optional.of(prenotazione) : Optional.empty();

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    }

// ==================== DELETE ====================

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM prenotazioni WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("Prenotazione deleted with ID: " + id);
                return true;
            } else {
                log.debug("No prenotazione found to delete with ID: " + id);
                return false;
            }

        } catch (SQLException e) {
            log.error("Error deleting prenotazione with ID: " + id, e);
            throw new RuntimeException("Error deleting prenotazione with ID: " + id, e);
        }
    }

    @Override
    public int deleteAll() {
        String sql = "DELETE FROM prenotazioni";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int rowsAffected = ps.executeUpdate();
            log.info("Deleted " + rowsAffected + " prenotazioni.");
            return rowsAffected;

        } catch (SQLException e) {
            log.error("Error deleting all prenotazioni", e);
            throw new RuntimeException("Error deleting all prenotazioni", e);
        }
    }


// ==================== UTILITY ====================

    private Prenotazione mapResultSetToPrenotazione(ResultSet rs) throws SQLException {
        long idUtente = rs.getLong("utente_id");
        long idAbitazione = rs.getLong("abitazione_id");
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setId(rs.getLong("id"));
        prenotazione.setData_inizio(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("data_inizio")));
        prenotazione.setData_fine(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("data_fine")));
        prenotazione.setUtente(utenteDAO.findById(idUtente)
        .orElseThrow(() -> new UserNotFoundException("Utente non trovato")));
        prenotazione.setAbitazione(abitazioneDAO.findById(idAbitazione)
        .orElseThrow(() -> new AbitazioneNotFoundException("Abitazione non trovato")));
        return prenotazione;
    }
    
}
