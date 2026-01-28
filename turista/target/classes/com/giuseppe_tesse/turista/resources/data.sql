-- Inserimento utenti normali
INSERT INTO users (first_name, last_name, email, password, address, registration_date) VALUES
('Mario', 'Rossi', 'mario.rossi@email.com', 'password123', 'Via Roma 1, Milano', '2024-01-10'),
('Laura', 'Bianchi', 'laura.bianchi@email.com', 'password123', 'Corso Italia 25, Roma', '2024-02-15'),
('Paolo', 'Verdi', 'paolo.verdi@email.com', 'password123', 'Via Garibaldi 10, Napoli', '2024-03-20'),
('Giulia', 'Neri', 'giulia.neri@email.com', 'password123', 'Piazza Duomo 5, Firenze', '2024-04-05'),
('Andrea', 'Blu', 'andrea.blu@email.com', 'password123', 'Via Torino 15, Bologna', '2024-05-12'),
('Francesca', 'Gialli', 'francesca.gialli@email.com', 'password123', 'Via Dante 8, Genova', '2024-06-18'),
('Luca', 'Viola', 'luca.viola@email.com', 'password123', 'Corso Vittorio 12, Torino', '2024-07-22'),
('Sara', 'Arancio', 'sara.arancio@email.com', 'password123', 'Via Manzoni 20, Palermo', '2024-08-30');

-- Inserimento utenti che diventeranno host
INSERT INTO users (first_name, last_name, email, password, address, registration_date) VALUES
('Giovanni', 'Ferrari', 'giovanni.ferrari@email.com', 'passHost1', 'Via Venezia 20, Venezia', '2023-10-01'),
('Marco', 'Colombo', 'marco.colombo@email.com', 'passHost2', 'Via Milano 8, Como', '2023-11-05'),
('Elena', 'Ricci', 'elena.ricci@email.com', 'passHost3', 'Via Napoli 12, Amalfi', '2023-12-10'),
('Chiara', 'Russo', 'chiara.russo@email.com', 'passHost4', 'Via Firenze 30, Siena', '2024-01-05'),
('Roberto', 'Costa', 'roberto.costa@email.com', 'passHost5', 'Via Genova 15, Portofino', '2024-02-01');

-- Promozione utenti a host (user_id 9-13)
INSERT INTO hosts (user_id, host_code, total_bookings, registration_date) VALUES
(9, 'HOST001', 0, '2023-10-01'),
(10, 'HOST002', 0, '2023-11-05'),
(11, 'HOST003', 125, '2023-12-10'), -- Esempio super_host simulato con total_bookings
(12, 'HOST004', 0, '2024-01-05'),
(13, 'HOST005', 0, '2024-02-01');

-- Inserimento abitazioni (Residences)
INSERT INTO residences (name, address, number_of_rooms, number_of_beds, floor, price, availability_start, availability_end, host_id) VALUES
-- Giovanni Ferrari (Host 9)
('Villa sul Lago', 'Via del Lago 5, Como', 5, 8, 0, 250.00, '2025-01-01', '2025-12-31', 9),
('Appartamento Centro', 'Piazza San Marco 10, Venezia', 3, 4, 2, 180.00, '2025-01-01', '2025-12-31', 9),
('Loft Moderno', 'Via Garibaldi 15, Venezia', 2, 2, 3, 150.00, '2025-01-01', '2025-12-31', 9),
-- Marco Colombo (Host 10)
('Chalet in Montagna', 'Via Alpina 20, Como', 4, 6, 0, 200.00, '2025-01-01', '2025-12-31', 10),
('Casa Rustica', 'Via Provinciale 8, Bellagio', 4, 5, 0, 160.00, '2025-01-01', '2025-12-31', 10),
-- Elena Ricci (Host 11)
('Attico Panoramico', 'Via Panoramica 10, Firenze', 6, 10, 5, 300.00, '2025-01-01', '2025-12-31', 11),
('Villa con Piscina', 'Via del Mare 25, Amalfi', 7, 12, 0, 400.00, '2025-01-01', '2025-12-31', 11),
('Monolocale Elegante', 'Via Tornabuoni 5, Firenze', 1, 2, 1, 120.00, '2025-01-01', '2025-12-31', 11),
-- Chiara Russo (Host 12)
('Casale Toscano', 'Strada Provinciale 12, Siena', 8, 14, 0, 450.00, '2025-01-01', '2025-12-31', 12),
('Appartamento Storico', 'Piazza del Campo 3, Siena', 3, 4, 1, 140.00, '2025-01-01', '2025-12-31', 12),
-- Roberto Costa (Host 13)
('Villa sul Mare', 'Lungomare 50, Portofino', 6, 10, 0, 350.00, '2025-01-01', '2025-12-31', 13),
('Mansarda Romantica', 'Via Castello 8, Portofino', 2, 2, 4, 180.00, '2025-01-01', '2025-12-31', 13);

-- Inserimento prenotazioni (Bookings)
INSERT INTO bookings (start_date, end_date, residence_id, user_id) VALUES
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

-- Inserimento feedback (Feedbacks)
INSERT INTO feedbacks (title, text, rating, booking_id, user_id) VALUES
('Soggiorno fantastico', 'Villa bellissima con vista mozzafiato sul lago. Host molto disponibile.', 5, 1, 1),
('Ottima posizione', 'Appartamento in pieno centro, comodissimo per visitare Venezia.', 4, 2, 2),
('Esperienza top', 'Chalet perfetto per una settimana sulla neve. Tutto come da descrizione.', 5, 3, 3),
('Consigliato!', 'Attico spazioso e luminoso, vista incredibile su Firenze.', 5, 4, 4),
('Meraviglioso', 'La villa è ancora più bella dal vivo. Piscina fantastica.', 5, 5, 5),
('Molto carino', 'Loft moderno e accogliente. Unico neo: un po rumoroso.', 4, 6, 6);


-- Residence più gettonata nell'ultimo mese
SELECT r.id, r.name, COUNT(b.id) as num_bookings
FROM residences r
JOIN bookings b ON r.id = b.residence_id
WHERE b.start_date >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY r.id, r.name
ORDER BY num_bookings DESC
LIMIT 1;

-- Host con più prenotazioni nell'ultimo mese
SELECT h.user_id, u.first_name, u.last_name, h.host_code, COUNT(b.id) as num_bookings
FROM hosts h
JOIN users u ON h.user_id = u.id
JOIN residences r ON r.host_id = h.user_id
JOIN bookings b ON b.residence_id = r.id
WHERE b.start_date >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY h.user_id, u.first_name, u.last_name, h.host_code
ORDER BY num_bookings DESC;

-- Punteggio medio (Rating) per Host
SELECT h.user_id, u.first_name, u.last_name,
       AVG(f.rating) as average_rating,
       COUNT(f.id) as total_feedbacks
FROM hosts h
JOIN users u ON h.user_id = u.id
JOIN residences r ON r.host_id = h.user_id
JOIN bookings b ON b.residence_id = r.id
LEFT JOIN feedbacks f ON f.booking_id = b.id
GROUP BY h.user_id, u.first_name, u.last_name;

-- Verifica sovrapposizioni (Overlapping bookings)
SELECT b1.*
FROM bookings b1
JOIN bookings b2 ON b1.residence_id = b2.residence_id 
    AND b1.id != b2.id
WHERE (b1.start_date BETWEEN b2.start_date AND b2.end_date)
   OR (b1.end_date BETWEEN b2.start_date AND b2.end_date)
   OR (b2.start_date BETWEEN b1.start_date AND b1.end_date);