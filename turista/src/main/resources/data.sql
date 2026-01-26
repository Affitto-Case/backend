-- Inserimento utenti normali
INSERT INTO utente (nome, cognome, email, indirizzo) VALUES
('Mario', 'Rossi', 'mario.rossi@email.com', 'Via Roma 1, Milano'),
('Laura', 'Bianchi', 'laura.bianchi@email.com', 'Corso Italia 25, Roma'),
('Paolo', 'Verdi', 'paolo.verdi@email.com', 'Via Garibaldi 10, Napoli'),
('Giulia', 'Neri', 'giulia.neri@email.com', 'Piazza Duomo 5, Firenze'),
('Andrea', 'Blu', 'andrea.blu@email.com', 'Via Torino 15, Bologna'),
('Francesca', 'Gialli', 'francesca.gialli@email.com', 'Via Dante 8, Genova'),
('Luca', 'Viola', 'luca.viola@email.com', 'Corso Vittorio 12, Torino'),
('Sara', 'Arancio', 'sara.arancio@email.com', 'Via Manzoni 20, Palermo');

-- Inserimento utenti che diventeranno host
INSERT INTO utente (nome, cognome, email, indirizzo) VALUES
('Giovanni', 'Ferrari', 'giovanni.ferrari@email.com', 'Via Venezia 20, Venezia'),
('Marco', 'Colombo', 'marco.colombo@email.com', 'Via Milano 8, Como'),
('Elena', 'Ricci', 'elena.ricci@email.com', 'Via Napoli 12, Amalfi'),
('Chiara', 'Russo', 'chiara.russo@email.com', 'Via Firenze 30, Siena'),
('Roberto', 'Costa', 'roberto.costa@email.com', 'Via Genova 15, Portofino');

-- Promozione utenti a host
INSERT INTO host (utente_id, codice_host) VALUES
(9, 'HOST001'),
(10, 'HOST002'),
(11, 'HOST003'),
(12, 'HOST004'),
(13, 'HOST005');

-- Inserimento abitazioni
INSERT INTO abitazione (nome, indirizzo, numero_locali, numero_posti_letto, piano, prezzo, disponibilita_inizio, disponibilita_fine, host_id) VALUES
-- Abitazioni HOST001 (Giovanni Ferrari)
('Villa sul Lago', 'Via del Lago 5, Como', 5, 8, 0, 250.00, '2025-01-01', '2025-12-31', 9),
('Appartamento Centro', 'Piazza San Marco 10, Venezia', 3, 4, 2, 180.00, '2025-01-01', '2025-12-31', 9),
('Loft Moderno', 'Via Garibaldi 15, Venezia', 2, 2, 3, 150.00, '2025-01-01', '2025-12-31', 9),

-- Abitazioni HOST002 (Marco Colombo)
('Chalet in Montagna', 'Via Alpina 20, Como', 4, 6, 0, 200.00, '2025-01-01', '2025-12-31', 10),
('Casa Rustica', 'Via Provinciale 8, Bellagio', 4, 5, 0, 160.00, '2025-01-01', '2025-12-31', 10),

-- Abitazioni HOST003 (Elena Ricci)
('Attico Panoramico', 'Via Panoramica 10, Firenze', 6, 10, 5, 300.00, '2025-01-01', '2025-12-31', 11),
('Villa con Piscina', 'Via del Mare 25, Amalfi', 7, 12, 0, 400.00, '2025-01-01', '2025-12-31', 11),
('Monolocale Elegante', 'Via Tornabuoni 5, Firenze', 1, 2, 1, 120.00, '2025-01-01', '2025-12-31', 11),

-- Abitazioni HOST004 (Chiara Russo)
('Casale Toscano', 'Strada Provinciale 12, Siena', 8, 14, 0, 450.00, '2025-01-01', '2025-12-31', 12),
('Appartamento Storico', 'Piazza del Campo 3, Siena', 3, 4, 1, 140.00, '2025-01-01', '2025-12-31', 12),

-- Abitazioni HOST005 (Roberto Costa)
('Villa sul Mare', 'Lungomare 50, Portofino', 6, 10, 0, 350.00, '2025-01-01', '2025-12-31', 13),
('Mansarda Romantica', 'Via Castello 8, Portofino', 2, 2, 4, 180.00, '2025-01-01', '2025-12-31', 13);

-- Inserimento prenotazioni (gennaio 2025)
INSERT INTO prenotazione (data_inizio, data_fine, abitazione_id, utente_id) VALUES
-- Gennaio
('2025-01-05', '2025-01-10', 1, 1),
('2025-01-12', '2025-01-15', 2, 2),
('2025-01-08', '2025-01-14', 4, 3),
('2025-01-10', '2025-01-17', 6, 4),
('2025-01-15', '2025-01-20', 7, 5),
('2025-01-18', '2025-01-22', 3, 6),
('2025-01-20', '2025-01-25', 9, 7),
('2025-01-22', '2025-01-28', 1, 8),
('2025-01-25', '2025-01-30', 5, 1),
('2025-01-27', '2025-01-31', 11, 2);

-- Inserimento feedback
INSERT INTO feedback (titolo, testo, punteggio, prenotazione_id, utente_id) VALUES
('Soggiorno fantastico', 'Villa bellissima con vista mozzafiato sul lago. Host molto disponibile e accogliente.', 5, 1, 1),
('Ottima posizione', 'Appartamento in pieno centro, comodissimo per visitare Venezia. Pulito e ben arredato.', 4, 2, 2),
('Esperienza top', 'Chalet perfetto per una settimana sulla neve. Tutto come da descrizione.', 5, 3, 3),
('Consigliato!', 'Attico spazioso e luminoso, vista incredibile su Firenze. Torneremo sicuramente.', 5, 4, 4),
('Meraviglioso', 'La villa è ancora più bella dal vivo. Piscina fantastica e giardino curatissimo.', 5, 5, 5),
('Molto carino', 'Loft moderno e accogliente, esattamente come nelle foto. Unico neo: un po rumoroso.', 4, 6, 6);

-- Aggiornamento contatore prenotazioni per simulazione
UPDATE super_host SET prenotazioni_totali = 125 WHERE host_id = 11;

-- Query di verifica
SELECT * FROM utente;
SELECT * FROM host;
SELECT * FROM super_host;
SELECT * FROM abitazione;
SELECT * FROM prenotazione;
SELECT * FROM feedback;

-- Query statistiche di test

-- Abitazione più gettonata nell'ultimo mese
SELECT a.id, a.nome, COUNT(p.id) as num_prenotazioni
FROM abitazione a
JOIN prenotazione p ON a.id = p.abitazione_id
WHERE p.data_inizio >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY a.id, a.nome
ORDER BY num_prenotazioni DESC
LIMIT 1;

-- Host con più prenotazioni nell'ultimo mese
SELECT h.utente_id, u.nome, u.cognome, h.codice_host, COUNT(p.id) as num_prenotazioni
FROM host h
JOIN utente u ON h.utente_id = u.id
JOIN abitazione a ON a.host_id = h.utente_id
JOIN prenotazione p ON p.abitazione_id = a.id
WHERE p.data_inizio >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY h.utente_id, u.nome, u.cognome, h.codice_host
ORDER BY num_prenotazioni DESC;

-- Top 5 utenti con più giorni prenotati nell'ultimo mese
SELECT u.id, u.nome, u.cognome, 
       SUM(p.data_fine - p.data_inizio) as giorni_totali
FROM utente u
JOIN prenotazione p ON u.id = p.utente_id
WHERE p.data_inizio >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY u.id, u.nome, u.cognome
ORDER BY giorni_totali DESC
LIMIT 5;

-- Media posti letto
SELECT AVG(numero_posti_letto) as media_posti_letto
FROM abitazione;


-- 1. Verifica sovrapposizioni prenotazioni (per validazione)
SELECT p1.*
FROM prenotazione p1
JOIN prenotazione p2 ON p1.abitazione_id = p2.abitazione_id 
    AND p1.id != p2.id
WHERE (p1.data_inizio BETWEEN p2.data_inizio AND p2.data_fine)
   OR (p1.data_fine BETWEEN p2.data_inizio AND p2.data_fine)
   OR (p2.data_inizio BETWEEN p1.data_inizio AND p1.data_fine);

-- 2. Ultima prenotazione per utente
SELECT DISTINCT ON (utente_id) *
FROM prenotazione
WHERE utente_id = ?
ORDER BY utente_id, created_at DESC;

-- 3. Abitazioni di un host specifico
SELECT a.*, 
       COUNT(DISTINCT p.id) as num_prenotazioni_totali
FROM abitazione a
LEFT JOIN prenotazione p ON a.id = p.abitazione_id
WHERE a.host_id = (SELECT utente_id FROM host WHERE codice_host = ?)
GROUP BY a.id;

-- 4. Punteggio medio per host
SELECT h.utente_id, u.nome, u.cognome,
       AVG(f.punteggio) as punteggio_medio,
       COUNT(f.id) as num_feedback
FROM host h
JOIN utente u ON h.utente_id = u.id
JOIN abitazione a ON a.host_id = h.utente_id
JOIN prenotazione p ON p.abitazione_id = a.id
LEFT JOIN feedback f ON f.prenotazione_id = p.id
GROUP BY h.utente_id, u.nome, u.cognome;

-- 5. Verifica se utente può lasciare feedback (ha soggiornato?)
SELECT COUNT(*)
FROM prenotazione p
WHERE p.utente_id = ?
  AND p.abitazione_id IN (
    SELECT id FROM abitazione WHERE host_id = ?
  )
  AND p.data_fine < CURRENT_DATE;