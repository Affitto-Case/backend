package com.giuseppe_tesse.turista.model;

import java.time.LocalDate;

public class Prenotazione {
    private long id;
    private Abitazione abitazione;
    private Utente utente;
    private LocalDate data_inizio;
    private LocalDate data_fine;
}
