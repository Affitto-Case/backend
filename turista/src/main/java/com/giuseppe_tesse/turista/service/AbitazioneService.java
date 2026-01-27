package com.giuseppe_tesse.turista.service;
import java.time.LocalDate;
import java.util.List;

import com.giuseppe_tesse.turista.dao.AbitazioneDAO;
import com.giuseppe_tesse.turista.exception.DuplicateAbitazioneException;
import com.giuseppe_tesse.turista.exception.AbitazioneNotFoundException;
import com.giuseppe_tesse.turista.model.Abitazione;
import com.giuseppe_tesse.turista.model.Host;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AbitazioneService {

    private final AbitazioneDAO abitazioneDAO;

    public AbitazioneService(AbitazioneDAO abitazioneDAO) {
        this.abitazioneDAO = abitazioneDAO;
    }

// ==================== CREATE ====================

    public Abitazione insertAbitazione(String nome,String indirizzo,double prezzo_per_notte,
                                      int numero_locali,int capacita_ospiti,
                                      int piano,LocalDate data_disponibilita_inizio,
                                      LocalDate data_disponibilita_fine,Host host) {
        log.info("Tentativo di inserimento abitazione - Nome: {}, Indirizzo: {}, Piano: {}", nome, indirizzo, piano);
        Abitazione abitazione = new Abitazione(nome,indirizzo,prezzo_per_notte,
                                              numero_locali,capacita_ospiti,
                                              piano,data_disponibilita_inizio,
                                              data_disponibilita_fine,host);
        if(abitazioneDAO.findByIndirizzoAndPiano(indirizzo, piano).isPresent()){
            log.warn("Inserimento abitazione fallito - Abitazione già esistente con Indirizzo: {} e Piano: {}", indirizzo, piano);
            throw new DuplicateAbitazioneException("indirizzo e piano", indirizzo + ", " + piano);
        }
        return abitazioneDAO.create(abitazione);
    }

// ==================== READ ====================

    public List<Abitazione> getAllAbitazioni() {
        log.info("Recupero di tutte le abitazioni");
        return abitazioneDAO.findAll();
    }

    public List<Abitazione> getAbitazioneByProprietario(Long proprietario_id) {
        log.info("Ricerca abitazione per ID proprietario: {}", proprietario_id);
        return abitazioneDAO.findByProprietario(proprietario_id);
    }

    public Abitazione getAbitazioneById(Long id) {
        log.info("Ricerca abitazione per ID: {}", id);
        return abitazioneDAO.findById(id).orElseThrow(() -> {
            log.warn("Abitazione non trovata con ID: {}", id);
            return new AbitazioneNotFoundException(id);
        });
    }

    public Abitazione getAbitazioneByIndirizzoAndPiano(String indirizzo, int piano) {
        log.info("Ricerca abitazione per Indirizzo: {} e Piano: {}", indirizzo, piano);
        return abitazioneDAO.findByIndirizzoAndPiano(indirizzo, piano).orElseThrow(() -> {
            log.warn("Abitazione non trovata con Indirizzo: {} e Piano: {}", indirizzo, piano);
            return new AbitazioneNotFoundException("Abitazione non trovata con Indirizzo: " + indirizzo + " e Piano: " + piano);
        });
    }

// ==================== UPDATE ====================

    public Abitazione updateAbitazione(Abitazione abitazione) {
        log.info("Tentativo di aggiornamento abitazione con ID: {}", abitazione.getId());
        return abitazioneDAO.update(abitazione).orElseThrow(() -> {
            log.warn("Aggiornamento abitazione fallito - Abitazione non trovata con ID: {}", abitazione.getId());
            return new AbitazioneNotFoundException(abitazione.getId());
        });
    }

// ==================== DELETE ====================

    public int deleteAllAbitazioni() {
        log.info("Eliminazione di tutte le abitazioni");
        return abitazioneDAO.deleteAll();
    }

    public boolean deleteAbitazioneById(Long id) {
        log.info("Tentativo di eliminazione abitazione con ID: {}", id);
        boolean deleted = abitazioneDAO.deleteById(id);
        if (!deleted) {
            log.warn("Eliminazione abitazione fallita - Abitazione non trovata con ID: {}", id);
            throw new AbitazioneNotFoundException(id);
        }
        return true;
    }

    public boolean deleteAbitazioneByProprietario(Long proprietario_id) {
        log.info("Tentativo di eliminazione abitazione con ID proprietario: {}", proprietario_id);
        boolean deleted = abitazioneDAO.deleteByProprietario(proprietario_id);
        if (!deleted) {
            log.warn("Eliminazione abitazione fallita - Abitazione non trovata con ID proprietario: {}", proprietario_id);
            throw new AbitazioneNotFoundException("Abitazione non trovata con ID proprietario: " + proprietario_id);
        }
        return true;
    }
    
}
