package com.giuseppe_tesse.turista.service;
import java.time.LocalDateTime;
import java.util.List;

import com.giuseppe_tesse.turista.dao.PrenotazioneDAO;
import com.giuseppe_tesse.turista.exception.DuplicatePrenotazioneException;
import com.giuseppe_tesse.turista.exception.PrenotazioneNotFoundException;
import com.giuseppe_tesse.turista.model.Abitazione;
import com.giuseppe_tesse.turista.model.Prenotazione;
import com.giuseppe_tesse.turista.model.Utente;
import com.giuseppe_tesse.turista.exception.AbitazioneNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrenotazioneService {

    private final PrenotazioneDAO prenotazioneDAO;

    public PrenotazioneService(PrenotazioneDAO prenotazioneDAO) {
        this.prenotazioneDAO = prenotazioneDAO;
    }

// ==================== CREATE ====================

 public Prenotazione insertPrenotazione(Abitazione abitazione,Utente utente,LocalDateTime dataInizio,LocalDateTime dataFine) {

    log.info("Tentativo di inserimento prenotazione - Abitazione ID: {}, Utente ID: {}, Data Inizio: {}, Data Fine: {}",
            abitazione.getId(), utente.getId(), dataInizio, dataFine);

    List<Prenotazione> esistenti =
            prenotazioneDAO.findByPrenotazioneId(abitazione.getId())
            .orElseThrow(() -> new PrenotazioneNotFoundException(abitazione.getId()));

    for (Prenotazione p : esistenti) {
        if (dataInizio.isBefore(p.getData_inizio()) &&
            dataFine.isAfter(p.getData_fine())) {

            throw new DuplicatePrenotazioneException(
                "Esiste già una prenotazione in quel periodo"
            );
        }
    }

    Prenotazione prenotazione =
            new Prenotazione(abitazione, utente, dataInizio, dataFine);

    return prenotazioneDAO.create(prenotazione);
}

// ==================== READ ====================

    public List<Prenotazione> getAllPrenotazioni() {
        log.info("Recupero di tutte le prenotazioni");
        return prenotazioneDAO.findAll();
    }

    public Prenotazione getPrenotazioneById(Long id) {
        log.info("Recupero della prenotazione con ID: {}", id);
        return prenotazioneDAO.findById(id)
                .orElseThrow(() -> new PrenotazioneNotFoundException(id));
    }

    public List<Prenotazione> getPrenotazioniByAbitazioneId(Long abitazioneId) {
        log.info("Recupero delle prenotazioni per l'abitazione con ID: {}", abitazioneId);
        return prenotazioneDAO.findByPrenotazioneId(abitazioneId)
            .orElseThrow(() -> new PrenotazioneNotFoundException(abitazioneId));
    }

// ==================== UPDATE ====================

    public Prenotazione updatePrenotazione(Prenotazione prenotazione) {
        log.info("Aggiornamento della prenotazione con ID: {}", prenotazione.getId());
        return prenotazioneDAO.update(prenotazione)
                .orElseThrow(() -> new PrenotazioneNotFoundException(prenotazione.getId()));
    }


// ==================== DELETE ====================

    public boolean deletePrenotazioneById(Long id) {
        log.info("Tentativo di eliminazione della prenotazione con ID: {}", id);
        boolean deleted = prenotazioneDAO.deleteById(id);
        if (!deleted) {
            log.warn("Eliminazione fallita - Prenotazione non trovata con ID: {}", id);
            throw new PrenotazioneNotFoundException(id);
        }
        return true;
    }

    public int deleteAllPrenotazioni() {
        log.info("Cancellazione di tutte le prenotazioni");
        return prenotazioneDAO.deleteAll();
    }
    
}
