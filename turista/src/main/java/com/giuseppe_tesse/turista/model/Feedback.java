package com.giuseppe_tesse.turista.model;

public class Feedback {
    private long id;
    private Prenotazione prenotazione;
    private String titolo;
    private int valutazione;
    private String commento;

    public Feedback(long id, Prenotazione prenotazione, String titolo, int valutazione, String commento) {
        this.id = id;
        this.titolo = titolo;
        this.prenotazione = prenotazione;
        this.valutazione = valutazione;
        this.commento = commento;
    }
    
}
