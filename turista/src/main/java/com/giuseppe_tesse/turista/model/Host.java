package com.giuseppe_tesse.turista.model;
import java.time.LocalDate;

public class Host extends Utente {

    private String codice_host;
    private int prenotazioni_totali;
    private LocalDate data_registrazione;

    public Host(long id, String nome, String cognome, String email, String password, String indirizzo,
                String codice_host, int prenotazioni_totali, LocalDate data_registrazione) {
        super(id, nome, cognome, email, password, indirizzo);
        this.codice_host = codice_host;
        this.prenotazioni_totali = prenotazioni_totali;
        this.data_registrazione = data_registrazione;
    }
    
}
