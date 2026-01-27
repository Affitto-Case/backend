package com.giuseppe_tesse.turista.model;  
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Utente {
    @Getter @Setter private Long id;
    @Getter @Setter private String nome;
    @Getter @Setter private String cognome;
    @Getter @Setter private String email;
    @Getter @Setter private String password;
    @Getter @Setter private String indirizzo;
    @Getter @Setter private LocalDate data_registrazione;

    public Utente(String nome, String cognome, String email, String password, String indirizzo, LocalDate data_registrazione) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.data_registrazione = data_registrazione;
        this.indirizzo = indirizzo;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 13 * hash + Objects.hashCode(this.email);
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
        final Utente other = (Utente) obj;
        if (this.id != other.id) {
            return false;
        }
        return Objects.equals(this.email, other.email);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Utente{");
        sb.append("id=").append(id);
        sb.append(", nome=").append(nome);
        sb.append(", cognome=").append(cognome);
        sb.append(", email=").append(email);
        sb.append(", password=").append(password);
        sb.append(", indirizzo=").append(indirizzo);
        sb.append(", data_registrazione=").append(data_registrazione);
        sb.append('}');
        return sb.toString();
    }

    
    
}
