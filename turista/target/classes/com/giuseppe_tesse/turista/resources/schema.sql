-- Creazione database
CREATE DATABASE luxury_tourist;

-- Connessione al database (PostgreSQL)
\c luxury_tourist;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Aggiunta per compatibilità con il backend
    address VARCHAR(255),
    registration_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indice per ricerche veloci su email (es. login/check duplicati)
CREATE INDEX idx_users_email ON users(email);

CREATE TABLE hosts (
    user_id INT PRIMARY KEY,
    host_code VARCHAR(50) UNIQUE NOT NULL,
    total_bookings INT DEFAULT 0,
    is_super_host BOOLEAN DEFAULT FALSE,
    registration_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_hosts_code ON hosts(host_code);

CREATE TABLE residences (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    number_of_rooms INT CHECK (number_of_rooms > 0),
    number_of_beds INT CHECK (number_of_beds > 0),
    floor INT,
    price DECIMAL(10,2) CHECK (price > 0),
    availability_start DATE NOT NULL,
    availability_end DATE NOT NULL,
    host_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (host_id) REFERENCES hosts(user_id) ON DELETE CASCADE,
    CHECK (availability_end > availability_start)
);

CREATE INDEX idx_residences_host ON residences(host_id);
CREATE INDEX idx_residences_price ON residences(price);
CREATE INDEX idx_residences_availability ON residences(availability_start, availability_end);

CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    residence_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (residence_id) REFERENCES residences(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (end_date > start_date)
);

CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_residence ON bookings(residence_id);
CREATE INDEX idx_bookings_dates ON bookings(start_date, end_date);

CREATE TABLE feedbacks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100),
    text TEXT,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    booking_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(booking_id) -- Garantisce un solo feedback per ogni prenotazione
);

CREATE INDEX idx_feedbacks_booking ON feedbacks(booking_id);
CREATE INDEX idx_feedbacks_user ON feedbacks(user_id);
CREATE INDEX idx_feedbacks_rating ON feedbacks(rating);