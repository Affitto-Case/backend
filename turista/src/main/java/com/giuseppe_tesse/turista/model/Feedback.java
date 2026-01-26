package com.giuseppe_tesse.turista.model;

import lombok.Getter;
import lombok.Setter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {
    @Getter private Long id;
    @Getter @Setter private Prenotazione prenotazione;
    @Getter @Setter private String titolo;
    @Getter @Setter private int valutazione;
    @Getter @Setter private String commento;

    public Feedback(Prenotazione prenotazione, String titolo, int valutazione, String commento) {
        this.titolo = titolo;
        this.prenotazione = prenotazione;
        this.valutazione = valutazione;
        this.commento = commento;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
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
        final Feedback other = (Feedback) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Feedback{");
        sb.append("id=").append(id);
        sb.append(", prenotazione=").append(prenotazione);
        sb.append(", titolo=").append(titolo);
        sb.append(", valutazione=").append(valutazione);
        sb.append(", commento=").append(commento);
        sb.append('}');
        return sb.toString();
    }


    
    
}
