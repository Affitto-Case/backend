package com.giuseppe_tesse.turista.model;
import java.time.LocalDate;

public class Abitazione {
    private long id;
    private String nome;
    private String indirizzo;
    private double prezzo_per_notte;
    private int numero_locali;
    private int capacita_ospiti;
    private int piano;
    private LocalDate data_disponibilita_inizio;
    private LocalDate data_disponibilita_fine;
    private Host host;

    public Abitazione(long id, String nome, String indirizzo, double prezzo_per_notte, int numero_locali,
                     int capacita_ospiti, int piano, LocalDate data_disponibilita_inizio,
                     LocalDate data_disponibilita_fine, Host host) {
        this.id = id;
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

    

}