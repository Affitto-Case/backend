package com.giuseppe_tesse.turista.model;




public class Utente {
    private final long id;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String indirizzo;

    public Utente(long id, String nome, String cognome, String email, String password, String indirizzo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.indirizzo = indirizzo;
    }


    
}
