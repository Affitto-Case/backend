package com.giuseppe_tesse.turista.service;
import com.giuseppe_tesse.turista.dao.UtenteDAO;
import com.giuseppe_tesse.turista.exception.DuplicateUserException;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import com.giuseppe_tesse.turista.model.Utente;

import java.time.LocalDate;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UtenteService {

    private final UtenteDAO utenteDAO;

    public UtenteService(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

// ==================== CREATE ====================

    public Utente insertUtente(String nome, String cognome, String email, String password,String indirizzo) {
        log.info("Tentativo di inserimento utente - Nome: {}, Cognome: {}, Email: {}", nome, cognome, email);
        Utente utente = new Utente(nome,cognome,email,password,indirizzo,LocalDate.now());

        if (utenteDAO.findByEmail(email) != null) {
            log.warn("Inserimento utente fallito - Email già esistente: {}", email);
            throw new DuplicateUserException("email", email);
        }

        return utenteDAO.create(utente);
    }

// ==================== READ ====================

    public List<Utente> getAllUtenti() {
        log.info("Recupero di tutti gli utenti");
        return utenteDAO.findAll();
    }

    public Utente getUtenteById(Long id) {
        log.info("Ricerca utente per ID: {}", id);
        return utenteDAO.findById(id).orElseThrow(() -> {
            log.warn("Utente non trovato con ID: {}", id);
            return new UserNotFoundException(id);
        });
    }

    public Utente getUtenteByEmail(String email) {
        log.info("Ricerca utente per email: {}", email);
        return utenteDAO.findByEmail(email).orElseThrow(() -> {
            log.warn("Utente non trovato con email: {}", email);
            return new UserNotFoundException(email);
        });
    }


// ==================== UPDATE ====================

    public Utente updateUtente(Utente utente) {
        log.info("Aggiornamento utente con ID: {}", utente.getId());
        
        if (utenteDAO.findById(utente.getId()).isEmpty()) {
            log.warn("Aggiornamento utente fallito - Utente non trovato con ID: {}", utente.getId());
            throw new UserNotFoundException(utente.getId());
        }
        return utenteDAO.update(utente).orElseThrow(() -> new UserNotFoundException(utente.getId()));
    }
    

// ==================== DELETE ====================

    public int deleteAllUtenti() {
        log.info("Cancellazione di tutti gli utenti");
        return utenteDAO.deleteAll();
    }

    public boolean deleteUtenteById(Long id) {
        log.info("Cancellazione utente per ID: {}", id);
        if (utenteDAO.findById(id).isEmpty()) {
            log.warn("Cancellazione utente fallita - Utente non trovato con ID: {}", id);
            throw new UserNotFoundException(id);
        }
        return utenteDAO.deleteById(id);
    }

    public boolean deleteUtenteByEmail(String email) {
        log.info("Cancellazione utente per email: {}", email);
        if (utenteDAO.findByEmail(email).isEmpty()) {
            log.warn("Cancellazione utente fallita - Utente non trovato con email: {}", email);
            throw new UserNotFoundException(email);
        }
        return utenteDAO.deleteByEmail(email);
    }
}


