-- Creazione database
CREATE DATABASE IF NOT EXISTS turista_facoltoso;

-- Connessione al database (PostgreSQL)
\c turista_facoltoso;

-- Tabella UTENTE
CREATE TABLE utente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    indirizzo VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indice per ricerche frequenti su email
CREATE INDEX idx_utente_email ON utente(email);

-- Tabella HOST
CREATE TABLE host (
    utente_id INT PRIMARY KEY,
    codice_host VARCHAR(50) UNIQUE NOT NULL,
    data_registrazione DATE NOT NULL,
    prenotazioni_totali INT DEFAULT 0 CHECK (prenotazioni_totali >= 100),
    FOREIGN KEY (utente_id) REFERENCES utente(id)
        ON DELETE CASCADE
);

-- Indice su codice_host per ricerche
CREATE INDEX idx_host_codice ON host(codice_host);

-- Tabella ABITAZIONE
CREATE TABLE abitazione (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    indirizzo VARCHAR(255) NOT NULL,
    numero_locali INT CHECK (numero_locali > 0),
    numero_posti_letto INT CHECK (numero_posti_letto > 0),
    piano INT,
    prezzo DECIMAL(8,2) CHECK (prezzo > 0),
    disponibilita_inizio DATE NOT NULL,
    disponibilita_fine DATE NOT NULL,
    host_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (host_id) REFERENCES host(utente_id)
        ON DELETE CASCADE,
    CHECK (disponibilita_fine > disponibilita_inizio)
);

-- Indici per query frequenti
CREATE INDEX idx_abitazione_host ON abitazione(host_id);
CREATE INDEX idx_abitazione_prezzo ON abitazione(prezzo);
CREATE INDEX idx_abitazione_disponibilita ON abitazione(disponibilita_inizio, disponibilita_fine);

-- Tabella PRENOTAZIONE
CREATE TABLE prenotazione (
    id SERIAL PRIMARY KEY,
    data_inizio DATE NOT NULL,
    data_fine DATE NOT NULL,
    abitazione_id INT NOT NULL,
    utente_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (abitazione_id) REFERENCES abitazione(id)
        ON DELETE CASCADE,
    FOREIGN KEY (utente_id) REFERENCES utente(id)
        ON DELETE CASCADE,
    CHECK (data_fine > data_inizio)
);

-- Indici per performance
CREATE INDEX idx_prenotazione_utente ON prenotazione(utente_id);
CREATE INDEX idx_prenotazione_abitazione ON prenotazione(abitazione_id);
CREATE INDEX idx_prenotazione_date ON prenotazione(data_inizio, data_fine);
CREATE INDEX idx_prenotazione_created ON prenotazione(created_at);

-- Tabella FEEDBACK
CREATE TABLE feedback (
    id SERIAL PRIMARY KEY,
    titolo VARCHAR(100),
    testo TEXT,
    punteggio INT NOT NULL CHECK (punteggio BETWEEN 1 AND 5),
    prenotazione_id INT NOT NULL,
    utente_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (prenotazione_id) REFERENCES prenotazione(id)
        ON DELETE CASCADE,
    FOREIGN KEY (utente_id) REFERENCES utente(id)
        ON DELETE CASCADE,
    UNIQUE(prenotazione_id) -- Un solo feedback per prenotazione
);

-- Indici per query frequenti
CREATE INDEX idx_feedback_prenotazione ON feedback(prenotazione_id);
CREATE INDEX idx_feedback_utente ON feedback(utente_id);
CREATE INDEX idx_feedback_punteggio ON feedback(punteggio);