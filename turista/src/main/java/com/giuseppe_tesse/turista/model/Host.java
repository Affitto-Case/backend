package com.giuseppe_tesse.turista.model;
import java.time.LocalDate;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Host extends Utente {

    @Getter private String codice_host;
    @Getter @Setter private int prenotazioni_totali;

    public Host(String nome, String cognome, String email, String password, String indirizzo,
                String codice_host, int prenotazioni_totali, LocalDate data_registrazione) {
        super(nome, cognome, email, password, indirizzo,data_registrazione);
        this.codice_host = codice_host;
        this.prenotazioni_totali = prenotazioni_totali;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.codice_host);
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
        final Host other = (Host) obj;
        return Objects.equals(this.codice_host, other.codice_host);
    }

    @Override
    public String toString() {
        return super.toString() + " Host [codice_host=" + codice_host + ", prenotazioni_totali=" + prenotazioni_totali + "]";
    }
    
    
}
