package com.giuseppe_tesse.turista.model;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

public class Prenotazione {
    @Getter private final long id;
    @Getter @Setter private Abitazione abitazione;
    @Getter @Setter private Utente utente;
    @Getter @Setter private LocalDate data_inizio;
    @Getter @Setter private LocalDate data_fine;
    
    public Prenotazione(long id, Abitazione abitazione, Utente utente, LocalDate data_inizio, LocalDate data_fine) {
        this.id = id;
        this.abitazione = abitazione;
        this.utente = utente;
        this.data_inizio = data_inizio;
        this.data_fine = data_fine;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Prenotazione other = (Prenotazione) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Prenotazione{");
        sb.append("id=").append(id);
        sb.append(", abitazione=").append(abitazione);
        sb.append(", utente=").append(utente);
        sb.append(", data_inizio=").append(data_inizio);
        sb.append(", data_fine=").append(data_fine);
        sb.append('}');
        return sb.toString();
    }



}
