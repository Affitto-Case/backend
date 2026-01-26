package com.giuseppe_tesse.turista.model;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Abitazione {
    @Getter private Long id;
    @Getter @Setter private String nome;
    @Getter @Setter private String indirizzo;
    @Getter @Setter private double prezzo_per_notte;
    @Getter @Setter private int numero_locali;
    @Getter @Setter private int capacita_ospiti;
    @Getter @Setter private int piano;
    @Getter @Setter private LocalDate data_disponibilita_inizio;
    @Getter @Setter private LocalDate data_disponibilita_fine;
    @Getter @Setter private Host host;

    public Abitazione(String nome, String indirizzo, double prezzo_per_notte, int numero_locali,
                     int capacita_ospiti, int piano, LocalDate data_disponibilita_inizio,
                     LocalDate data_disponibilita_fine, Host host) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.prezzo_per_notte = prezzo_per_notte;
        this.numero_locali = numero_locali;
        this.capacita_ospiti = capacita_ospiti;
        this.piano = piano;
        this.data_disponibilita_inizio = data_disponibilita_inizio;
        this.data_disponibilita_fine = data_disponibilita_fine;
        this.host = host;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (int) (this.id ^ (this.id >>> 32));
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
        final Abitazione other = (Abitazione) obj;
        return this.id == other.id; 
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Abitazione{");
        sb.append("id=").append(id);
        sb.append(", nome=").append(nome);
        sb.append(", indirizzo=").append(indirizzo);
        sb.append(", prezzo_per_notte=").append(prezzo_per_notte);
        sb.append(", numero_locali=").append(numero_locali);
        sb.append(", capacita_ospiti=").append(capacita_ospiti);
        sb.append(", piano=").append(piano);
        sb.append(", data_disponibilita_inizio=").append(data_disponibilita_inizio);
        sb.append(", data_disponibilita_fine=").append(data_disponibilita_fine);
        sb.append(", host=").append(host);
        sb.append('}');
        return sb.toString();
    }

}