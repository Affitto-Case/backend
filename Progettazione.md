# Progetto "Turista Facoltoso" - Documentazione di Progettazione

## Indice
1. [Panoramica del Progetto](#panoramica-del-progetto)
2. [Analisi dei Requisiti](#analisi-dei-requisiti)
3. [Architettura del Sistema](#architettura-del-sistema)
4. [Modello Dati](#modello-dati)
5. [API Backend](#api-backend)
6. [Struttura Frontend](#struttura-frontend)
7. [Database](#database)
8. [Piano di Sviluppo](#piano-di-sviluppo)

---

## Panoramica del Progetto

### Descrizione
Sistema di gestione backoffice per una piattaforma di affitto case vacanza. L'operatore può registrare e gestire utenti, host, abitazioni, prenotazioni e feedback senza necessità di autenticazione.

### Stack Tecnologico
- **Backend**: Java
- **Frontend**: React
- **Database**: PostgreSQL
- **Version Control**: Git/GitHub

### Obiettivi Principali
- CRUD completo per tutte le entità
- Query analitiche per statistiche e reportistica
- Interfaccia utente intuitiva e responsive
- Persistenza dati su database relazionale

---

## Analisi dei Requisiti

### Requisiti Funzionali

#### RF1 - Gestione Utenti
- **RF1.1**: Creare un nuovo utente
- **RF1.2**: Modificare i dati di un utente
- **RF1.3**: Eliminare un utente
- **RF1.4**: Visualizzare tutti gli utenti
- **RF1.5**: Promuovere un utente a host
- **RF1.6**: Promuovere un host a super-host (quando raggiunge 100 prenotazioni)

#### RF2 - Gestione Host
- **RF2.1**: Visualizzare tutti gli host
- **RF2.2**: Visualizzare i super-host
- **RF2.3**: Associare abitazioni a un host
- **RF2.4**: Aggiornare il contatore prenotazioni per super-host

#### RF3 - Gestione Abitazioni
- **RF3.1**: Creare una nuova abitazione
- **RF3.2**: Modificare i dati di un'abitazione
- **RF3.3**: Eliminare un'abitazione
- **RF3.4**: Visualizzare tutte le abitazioni
- **RF3.5**: Filtrare abitazioni per codice host

#### RF4 - Gestione Prenotazioni
- **RF4.1**: Creare una nuova prenotazione
- **RF4.2**: Modificare una prenotazione
- **RF4.3**: Eliminare una prenotazione
- **RF4.4**: Visualizzare tutte le prenotazioni
- **RF4.5**: Ottenere l'ultima prenotazione di un utente

#### RF5 - Gestione Feedback
- **RF5.1**: Creare un nuovo feedback
- **RF5.2**: Modificare un feedback
- **RF5.3**: Eliminare un feedback
- **RF5.4**: Visualizzare tutti i feedback
- **RF5.5**: Collegare feedback a prenotazioni

#### RF6 - Statistiche e Analisi
- **RF6.1**: Abitazione più gettonata nell'ultimo mese
- **RF6.2**: Host con più prenotazioni nell'ultimo mese
- **RF6.3**: Top 5 utenti con più giorni prenotati nell'ultimo mese
- **RF6.4**: Numero medio di posti letto per abitazione
- **RF6.5**: Ottenere tutti i super-host

### Requisiti Non Funzionali

#### RNF1 - Qualità del Codice
- Organizzazione in package (model, dao, controller, service, dto)
- Utilizzo di Collections appropriate (List, Map, Set)
- Gestione eccezioni personalizzate
- Logging delle operazioni principali (Log4j/SLF4J)

#### RNF2 - Performance
- Tempo di risposta API < 1 secondo per query semplici
- Tempo di risposta API < 3 secondi per query analitiche
- Utilizzo di indici database per query frequenti

#### RNF3 - Usabilità
- Interfaccia responsive (mobile, tablet, desktop)
- Feedback visivi per tutte le operazioni
- Messaggi di errore chiari e comprensibili
- Conferme per operazioni distruttive (eliminazioni)

#### RNF4 - Manutenibilità
- Codice documentato con JavaDoc
- Componenti React riutilizzabili
- Separazione delle responsabilità (SoC)
- RESTful API design

---

## Architettura del Sistema

### Architettura Backend (Java)

```
src/
├── main/
│ ├── java/
│ │ └── com/giuseppe_tesse/turista/
│ │ |   ├── LuxuryTouristApplication.java
│ │ ├── model/
│ │ │ ├── User.java
│ │ │ ├── Host.java
│ │ │ ├── Residence.java
│ │ │ ├── Booking.java
│ │ │ └── Feedback.java
│ │ ├── dao/
│ │ │ ├── UserDAO.java
│ │ │ ├── HostDAO.java
│ │ │ ├── ResidenceDAO.java
│ │ │ ├── BookingDAO.java
│ │ │ └── FeedbackDAO.java
│ │ ├── dto/
| | | ├── request/
│ │ │ |  ├── UserRequestDTO.java
│ │ │ |  ├── HostRequestDTO.java
│ │ │ |  ├── ResidenceRequestDTO.java
│ │ │ |  ├── BookingRequestDTO.java
│ │ │ |  └── FeedbackRequestDTO.java
| | | ├── response/
│ │ │ |  ├── UserResponseDTO.java
│ │ │ |  ├── HostResponseDTO.java
│ │ │ |  ├── ResidenceResponseDTO.java
│ │ │ |  ├── BookingResponseDTO.java
│ │ │ |  └── FeedbackResponseDTO.java
│ │ ├── router/
│ │ │ └── Router.java
│ │ ├── service/
│ │ │ ├── UserService.java
│ │ │ ├── HostService.java
│ │ │ ├── ResidenceService.java
│ │ │ ├── BookingService.java
│ │ │ ├── FeedbackService.java
│ │ │ └── StatisticsService.java
│ │ ├── controller/
│ │ │ ├── UserController.java
│ │ │ ├── HostController.java
│ │ │ ├── ResidenceController.java
│ │ │ ├── BookingController.java
│ │ │ ├── FeedbackController.java
│ │ │ └── StatisticsController.java
│ │ ├── exception/
│ │ │ ├── ResourceNotFoundException.java
│ │ │ ├── BadRequestException.java
│ │ │ ├── DuplicateResourceException.java
│ │ │ └── GlobalExceptionHandler.java
│ │ └── util/
│ │ ├── DatabaseConnection.java
│ │ └── DateUtils.java
│ └── resources/
│ ├── application.properties
│ ├── schema.sql
│ └── data.sql
### Pattern Utilizzati

#### 1. DAO Pattern
Separazione logica di accesso ai dati dal business logic.

```java
public interface UserDAO {
    User create(User user);
    User findById(Long id);
    User findByEmail(String email);
    List<User> findAll();
    User update(User user);
    void delete(Long id);
}
```

#### 2. Service Layer Pattern
Logica di business centralizzata.

```java
public class BookingService {
    private BookingDAO bookingDAO;
    private ResidenceDAO residenceDAO;
    
    public Booking createBooking(BookingDAOImpl booking) {
        // Validazione disponibilità
        // Controllo sovrapposizioni
        // Aggiornamento contatore super-host
        // Logging
    }
}
```
---

## Modello Dati

### Diagramma ER (Entità-Relazioni)

## Diagramma ER

```mermaid
erDiagram
    Users (
      id SERIAL PRIMARY KEY,
      first_name VARCHAR(255),
      last_name VARCHAR(255),
      email VARCHAR(255),
      password VARCHAR(255),
      address VARCHAR(255),
      registration_date DATE
    )

    Hosts (
    user_id SERIAL PRIMARY KEY REFERENCES Users(id),
    host_code VARCHAR(50),
    total_bookings INT,
    registration_date DATE
    );


    Residences (
      id SERIAL PRIMARY KEY,
      name VARCHAR(255),
      address VARCHAR(255),
      number_of_rooms INT,
      number_of_beds INT,
      floor INT,
      price DECIMAL(10,2),
      availability_start DATE,
      availability_end DATE,
      host_id BIGINT REFERENCES Hosts(user_id)
);


    Bookings (
      id SERIAL PRIMARY KEY,
      start_date DATE,
      end_date DATE,
      residence_id INT REFERENCES Residences(id),
      user_id INT REFERENCES Users(id)
);


    Feedbacks (
      id SERIAL PRIMARY KEY,
      title VARCHAR(255),
      text TEXT,
      rating INT,
      booking_id INT REFERENCES Bookings(id),
      user_id INT REFERENCES Users(id)
);


    Users ||--o{ Bookings : makes
    Residences ||--o{ Bookings : is_booked
    Hosts ||--o{ Residences : manages
    Bookings ||--o{ Feedbacks : generates
    Users ||--o{ Feedbacks : leaves

    Users ||--|| Hosts : "is a"
    Hosts ||--|| SuperHosts : "is a"
```

### Entity Descriptions

#### USERS
- **id**: SERIAL, primary key
- **first_name**: VARCHAR(100), NOT NULL
- **last_name**: VARCHAR(100), NOT NULL
- **email**: VARCHAR(150), UNIQUE, NOT NULL
- **address**: VARCHAR(255), nullable

**Relationships**: 
- A user can become a host (1:1 optional with HOSTS)
- A user can make many bookings (1:N with BOOKINGS)
- A user can leave many feedbacks (1:N with FEEDBACKS)

---

#### HOSTS
- **user_id**: INT, primary key and foreign key → USERS(id)
- **host_code**: VARCHAR(50), UNIQUE, NOT NULL
- **total_bookings**: INT, DEFAULT 0
- **registration_date**: DATE
- **is_super_host**: BOOLEAN DEFAULT False

**Relationships**:
- A host is also a user (1:1 with USERS)
- A host can have many residences (1:N with RESIDENCES)
- A host can become a super-host (1:1 optional with SUPER_HOSTS)


---

#### RESIDENCES
- **id**: SERIAL, primary key
- **name**: VARCHAR(100), NOT NULL
- **address**: VARCHAR(255), NOT NULL
- **number_of_rooms**: INT
- **number_of_beds**: INT
- **floor**: INT
- **price**: DECIMAL(8,2)
- **availability_start**: DATE
- **availability_end**: DATE
- **host_id**: INT, FK → HOSTS(user_id), NOT NULL

**Relationships**:
- A residence belongs to a single host (N:1 with HOSTS)
- A residence can have many bookings (1:N with BOOKINGS)

---

#### BOOKINGS
- **id**: SERIAL, primary key
- **start_date**: DATE, NOT NULL
- **end_date**: DATE, NOT NULL
- **residence_id**: INT, FK → RESIDENCES(id)
- **user_id**: INT, FK → USERS(id)

**Relationships**:
- A booking concerns a residence (N:1 with RESIDENCES)
- A booking is made by a user (N:1 with USERS)
- A booking can have a feedback (1:1 optional with FEEDBACKS)

**Constraints**:
- `end_date` must be after `start_date`
- Overlapping bookings for the same residence are not allowed

---

#### FEEDBACKS
- **id**: SERIAL, primary key
- **title**: VARCHAR(100)
- **text**: TEXT
- **rating**: INT, CHECK (rating BETWEEN 1 AND 5)
- **booking_id**: INT, FK → BOOKINGS(id)
- **user_id**: INT, FK → USERS(id)

**Relationships**:
- A feedback is linked to a booking (N:1 with BOOKINGS)
- A feedback is left by a user (N:1 with USERS)

## API Backend

### Base URL
```
http://localhost:8080/api
```

### Convenzioni
- Tutte le risposte in formato JSON
- Uso corretto dei verbi HTTP (GET, POST, PUT, DELETE)
- Codici di stato HTTP appropriati
- Gestione errori consistente

---

### 1. Utenti

#### GET /users
Ottiene tutti gli utenti

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "first_name": "Mario",
    "last_name": "Rossi",
    "email": "mario.rossi@email.com",
    "address": "Via Roma 1, Milan",
  },
  {
    "id": 2,
    "first_name": "Giovanni",
    "last_name": "Bianchi",
    "email": "giovanni.bianchi@email.com",
    "address": "Via Venezia 20, Venice",
    "host_code": "HOST001",
    "total_bookings": 150
  }
]
```
#### GET /users/{id}
Ottiene un utente specifico

**Response 200 OK / 404 Not Found**

#### POST /user
Crea un nuovo utente

**Request Body:**
```json
{
  "nome": "Mario",
  "cognome": "Rossi",
  "email": "mario.rossi@email.com",
  "address": "Via Roma 1, Milano"
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "nome": "Mario",
  "cognome": "Rossi",
  "email": "mario.rossi@email.com",
  "address": "Via Roma 1, Milano",
}
```

**Validazioni:**
- Email obbligatoria e valida
- Email univoca
- Nome e cognome obbligatori

#### PUT /users/{id}
Aggiorna un utente

**Request Body:** Stesso formato del POST

**Response 200 OK / 404 Not Found**

#### DELETE /users/{id}
Elimina un utente

**Response 204 No Content / 404 Not Found**


#### GET /users/{id}/last-booking
Ottiene l'ultima prenotazione di un utente

**Response 200 OK:**
```json
{
  "id": 15,
  "start_date": "2025-02-10",
  "end_date": "2025-02-15",
  "residence": {
    "id": 3,
    "name": "Sea View Apartment",
    "address": "Via Mare 10, Rimini"
  },
  "total_days": 5,
  "total_price": 750.00
}

```

**Response 404:** Se l'utente non ha prenotazioni

---

### 2. Host

#### GET /hosts
Ottiene tutti gli host

**Response 200 OK:**
```json
[
  {
    "user_id": 2,
    "first_name": "Giovanni",
    "last_name": "Bianchi",
    "email": "giovanni.bianchi@email.com",
    "host_code": "HOST001",
    "number_of_residences": 5,
    "total_bookings": 150,
    "is_super_host": true,
    "super_host_registration_date": "2024-06-15"
  }
]

```

#### POST /host
Promuove un utente a host

**Request Body:**
```json
{
  "user_id ": 1,
  "host_code ": "HOST005"
}
```

**Response 201 Created**

**Validazioni:**
- L'utente deve esistere
- L'utente non deve essere già un host
- Il codice host deve essere univoco

#### GET /hosts/super-hosts
Ottiene tutti i super-host (≥100 prenotazioni)

**Response 200 OK:**
```json
[
  {
    "host_id": 2,
    "full_name": "Giovanni Bianchi",
    "host_code": "HOST001",
    "total_bookings": 150,
    "registration_date": "2024-06-15",
    "number_of_residences": 5
  }
]

```

#### GET /host/{codiceHost}/residences
Ottiene tutte le abitazioni di un host specifico

**Response 200 OK:** Array di abitazioni (vedi sezione Abitazioni)

---

### 3. Abitazioni

#### GET /residences
Ottiene tutte le abitazioni

**Query Parameters (opzionali):**
- `host_Id`: filtra per ID host
- `host_code`: filtra per codice host
- `minPrezzo`: prezzo minimo
- `maxPrezzo`: prezzo massimo
- `minPostiLetto`: numero minimo posti letto

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "name": "Lake Villa",
    "address": "Via del Lago 5, Como",
    "number_of_rooms": 5,
    "number_of_beds": 8,
    "floor": 0,
    "price": 250.00,
    "availability_start": "2025-01-01",
    "availability_end": "2025-12-31",
    "host": {
      "id": 2,
      "full_name": "Giovanni Bianchi",
      "host_code": "HOST001"
    }
  }
]

```

#### GET /residences/{id}
Ottiene un'abitazione specifica

**Response 200 OK / 404 Not Found**

#### POST /residences
Crea una nuova abitazione

**Request Body:**
```json
{
  "name": "Lake Villa",
  "address": "Via del Lago 5, Como",
  "number_of_rooms": 5,
  "number_of_beds": 8,
  "floor": 0,
  "price": 250.00,
  "availability_start": "2025-01-01",
  "availability_end": "2025-12-31",
  "host_id": 2
}

```

**Response 201 Created**

**Validazioni:**
- L'host deve esistere nella tabella HOST
- disponibilitaFine > disponibilitaInizio
- Prezzo > 0

#### PUT /residences/{id}
Aggiorna un'abitazione

**Request Body:** Stesso formato del POST

**Response 200 OK / 404 Not Found**

#### DELETE /residences/{id}
Elimina un'abitazione

**Response 204 No Content / 404 Not Found**

---

### 4. Prenotazioni

#### GET /bookings 
Ottiene tutte le prenotazioni

**Query Parameters (opzionali):**
- `utenteId`: filtra per utente
- `abitazioneId`: filtra per abitazione
- `hostId`: filtra per host
- `dataInizio`: filtra da data (formato YYYY-MM-DD)
- `dataFine`: filtra fino a data

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "start_date": "2025-02-01",
    "end_date": "2025-02-05",
    "user": {
      "id": 1,
      "first_name": "Mario",
      "last_name": "Rossi"
    },
    "residence": {
      "id": 3,
      "name": "City Apartment",
      "address": "Via Milano 5, Rome"
    },
    "total_days": 4,
    "total_price": 600.00,
  }
]

```

#### GET /booking/{id}
Ottiene una prenotazione specifica

**Response 200 OK / 404 Not Found**

#### POST /bookings
Crea una nuova prenotazione

**Request Body:**
```json
{
  "start_date": "2025-02-01",
  "end_date": "2025-02-05",
  "user_id": 1,
  "residence_id": 3
}

```

**Response 201 Created**

**Validazioni:**
- Utente e abitazione devono esistere
- dataFine > dataInizio
- L'abitazione deve essere disponibile nel periodo
- Non devono esistere prenotazioni sovrapposte per la stessa abitazione
- Le date devono essere nel periodo di disponibilità dell'abitazione

**Logica di business:**
- Incrementa il contatore prenotazioni_totali del super-host (se esiste)
- Se l'host raggiunge 100 prenotazioni, viene promosso a super-host automaticamente

#### PUT /bookings/{id}
Aggiorna una prenotazione

**Request Body:** Stesso formato del POST

**Response 200 OK / 404 Not Found**

**Note:** Esegue le stesse validazioni del POST

#### DELETE /bookings/{id}
Elimina una prenotazione

**Response 204 No Content / 404 Not Found**

**Note:** Decrementa il contatore del super-host se necessario

---

### 5. Feedback

#### GET /feedback
Ottiene tutti i feedback

**Query Parameters (opzionali):**
- `utenteId`: filtra per utente che ha lasciato il feedback
- `hostId`: filtra per host valutato
- `prenotazioneId`: filtra per prenotazione
- `minPunteggio`: punteggio minimo (1-5)

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "title": "Fantastic Stay",
    "text": "Great experience, host was helpful and the house was very clean",
    "rating": 5,
    "user": {
      "id": 1,
      "full_name": "Mario Rossi"
    },
    "booking": {
      "id": 10,
      "residence": "Lake Villa"
    },
    "host": {
      "id": 2,
      "full_name": "Giovanni Bianchi",
      "host_code": "HOST001"
    }
  }
]

```

#### GET /feedback/{id}
Ottiene un feedback specifico

**Response 200 OK / 404 Not Found**

#### POST /feedback
Crea un nuovo feedback

**Request Body:**
```json
{
  "title": "Great Stay",
  "text": "Beautiful house and very helpful host",
  "rating": 5,
  "booking_id": 10,
  "user_id": 1
}

```

**Response 201 Created**

**Validazioni:**
- Punteggio tra 1 e 5
- La prenotazione deve esistere
- L'utente deve esistere
- L'utente deve essere lo stesso della prenotazione
- La prenotazione non deve avere già un feedback

#### PUT /feedback/{id}
Aggiorna un feedback

**Request Body:**
```json
{
  "title": "Excellent Stay",
  "text": "Experience definitely worth repeating",
  "rating": 5
}

```

**Response 200 OK / 404 Not Found**

#### DELETE /feedback/{id}
Elimina un feedback

**Response 204 No Content / 404 Not Found**

---

### 6. Statistics

#### GET /statistics/most-booked-residence

Mostra le abitazioni con più prenotaioni nell'ultimo mese.

**Response 200 OK:**

```json
{
  "residence": {
    "id": 5,
    "name": "Penthouse with View",
    "address": "Via Panoramica 10, Florence",
    "price": 180.00
  },
  "total_bookings": 12,
  "total_days_booked": 48,
  "host": {
    "full_name": "Laura Verdi",
    "host_code": "HOST003"
  }
}
```

**SQL Query:**

```sql
SELECT r.*, COUNT(b.id) AS total_bookings, 
       SUM(b.end_date - b.start_date) AS total_days_booked
FROM residences r
JOIN bookings b ON r.id = b.residence_id
WHERE b.start_date >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY r.id
ORDER BY total_bookings DESC
LIMIT 1;
```

---

#### GET /statistics/top-hosts

Mostra gli host con più prenotazione del mese.

**Query Parameters (optional):**

* `limit`: maximum number of results (default: 10)

**Response 200 OK:**

```json
[
  {
    "host": {
      "id": 3,
      "first_name": "Laura",
      "last_name": "Verdi",
      "host_code": "HOST003"
    },
    "total_bookings": 25,
    "total_days_booked": 180,
    "number_of_residences": 7,
    "is_super_host": true
  }
]
```

**SQL Query:**

```sql
SELECT h.*, u.first_name, u.last_name, 
       COUNT(b.id) AS total_bookings,
       SUM(b.end_date - b.start_date) AS total_days_booked
FROM hosts h
JOIN users u ON h.user_id = u.id
JOIN residences r ON r.host_id = h.user_id
JOIN bookings b ON b.residence_id = r.id
WHERE b.start_date >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY h.user_id, u.first_name, u.last_name
ORDER BY total_bookings DESC
LIMIT 10;
```

---

#### GET /statistics/top-users

Mostra gli utenti con più giorni di prenotazione del mese.

**Response 200 OK:**

```json
[
  {
    "user": {
      "id": 7,
      "first_name": "Paolo",
      "last_name": "Neri",
      "email": "paolo.neri@email.com"
    },
    "days_booked": 28,
    "total_bookings": 4,
    "total_spent": 4200.00
  }
]
```

**SQL Query:**

```sql
SELECT u.*, 
       SUM(b.end_date - b.start_date) AS days_booked,
       COUNT(b.id) AS total_bookings,
       SUM((b.end_date - b.start_date) * r.price) AS total_spent
FROM users u
JOIN bookings b ON u.id = b.user_id
JOIN residences r ON b.residence_id = r.id
WHERE b.start_date >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY u.id
ORDER BY days_booked DESC
LIMIT 5;
```

---

#### GET /statistics/average-beds

Mostra il numero medio di posti letto delle abitazioni.

**Response 200 OK:**

```json
{
  "average_beds": 5.2,
  "total_residences": 150,
  "min_beds": 1,
  "max_beds": 12,
  "distribution": {
    "1-2": 25,
    "3-4": 60,
    "5-6": 40,
    "7+": 25
  }
}
```

**SQL Query:**

```sql
SELECT AVG(number_of_beds) AS average_beds,
       COUNT(*) AS total_residences,
       MIN(number_of_beds) AS min_beds,
       MAX(number_of_beds) AS max_beds
FROM residences;
```

---

#### GET /statistics/dashboard

Mostra un sommario completo delle statistiche.

**Response 200 OK:**

```json
{
  "overview": {
    "total_users": 250,
    "total_hosts": 45,
    "total_super_hosts": 8,
    "total_residences": 150,
    "total_bookings": 890,
    "total_feedbacks": 420
  },
  "last_month": {
    "new_bookings": 78,
    "new_users": 15,
    "new_residences": 8,
    "feedbacks_received": 62
  },
  "most_booked_residence": { /* ... */ },
  "top_hosts": [ /* ... */ ],
  "top_users": [ /* ... */ ],
  "average_beds": 5.2,
  "average_feedback_rating": 4.3
}
```

---

### Gestione Errori

Tutte le API seguono un formato standard per gli errori:

```json
{
  "timestamp": "2025-01-23T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Utente con id 999 non trovato",
  "path": "/api/utenti/999",
  "details": []
}
```

**Codici di Stato HTTP:**
- `200 OK`: Operazione riuscita (GET, PUT)
- `201 Created`: Risorsa creata con successo (POST)
- `204 No Content`: Risorsa eliminata con successo (DELETE)
- `400 Bad Request`: Dati non validi, validazione fallita
- `404 Not Found`: Risorsa non trovata
- `409 Conflict`: Conflitto (es. email duplicata, date sovrapposte)
- `500 Internal Server Error`: Errore del server

---

## Struttura Frontend

### Routing

```javascript
// App.jsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';

function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/utenti" element={<UtentiPage />} />
        <Route path="/host" element={<HostPage />} />
        <Route path="/abitazioni" element={<AbitazioniPage />} />
        <Route path="/prenotazioni" element={<PrenotazioniPage />} />
        <Route path="/feedback" element={<FeedbackPage />} />
        <Route path="/statistiche" element={<StatistichePage />} />
      </Routes>
      <Footer />
    </BrowserRouter>
  );
}
```

### Componenti Principali

#### 1. HomePage (Dashboard)
```jsx
// Mostra statistiche principali e link rapidi
- Card con totali (utenti, host, abitazioni, prenotazioni)
- Grafico prenotazioni ultimo mese
- Tabella super-host
- Link rapidi alle sezioni
```

#### 2. UtentiPage
```jsx
// Gestione completa utenti
- Tabella con tutti gli utenti
- Filtri: nome, cognome, email, tipo (tutti/host/super-host)
- Pulsante "Aggiungi Utente"
- Azioni: Modifica, Elimina, Promuovi a Host
- Modale per form creazione/modifica
```

#### 3. HostPage
```jsx
// Visualizzazione e gestione host
- Griglia card host con badge "Super Host"
- Filtri: codice host, numero abitazioni
- Dettagli: numero abitazioni, prenotazioni totali
- Link a abitazioni del host
```

#### 4. AbitazioniPage
```jsx
// Gestione abitazioni
- Griglia card abitazioni con immagine placeholder
- Filtri: host, prezzo, posti letto, disponibilità
- Pulsante "Aggiungi Abitazione"
- Card mostra: nome, indirizzo, prezzo, posti letto, host
- Modale dettagli con tutte le info
```

#### 5. PrenotazioniPage
```jsx
// Gestione prenotazioni
- Vista tabella e vista calendario
- Filtri: utente, abitazione, periodo
- Pulsante "Nuova Prenotazione"
- Verifica disponibilità in tempo reale
- Calcolo automatico prezzo totale
- Indicatore se ha feedback
```

#### 6. FeedbackPage
```jsx
// Gestione feedback
- Lista card feedback con stelle
- Filtri: punteggio, host, periodo
- Solo feedback collegati a prenotazioni valide
- Mostra utente, prenotazione, abitazione, host
```

#### 7. StatistichePage
```jsx
// Dashboard statistiche
- Sezione "Abitazione più gettonata"
- Sezione "Top Host del mese"
- Sezione "Top 5 Utenti per giorni prenotati"
- Card "Media posti letto"
- Grafici interattivi (Chart.js o Recharts)
```

### Componenti Riutilizzabili

#### UtenteForm
```jsx
function UtenteForm({ utente, onSave, onCancel }) {
  const [formData, setFormData] = useState({
    nome: '',
    cognome: '',
    email: '',
    indirizzo: ''
  });
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    // Validazione
    // Chiamata API
    // Gestione successo/errore
  };
  
  return (
    <form onSubmit={handleSubmit}>
      {/* Campi form */}
    </form>
  );
}
```

#### AbitazioneForm
```jsx
function AbitazioneForm({ abitazione, onSave, onCancel }) {
  // Selezione host da dropdown
  // Date picker per disponibilità
  // Validazione campi numerici
  // Controllo date (fine > inizio)
}
```

#### PrenotazioneForm
```jsx
function PrenotazioneForm({ prenotazione, onSave, onCancel }) {
  const [selectedUtente, setSelectedUtente] = useState(null);
  const [selectedAbitazione, setSelectedAbitazione] = useState(null);
  const [dataInizio, setDataInizio] = useState('');
  const [dataFine, setDataFine] = useState('');
  const [disponibile, setDisponibile] = useState(null);
  
  // Verifica disponibilità quando cambiano abitazione o date
  useEffect(() => {
    if (selectedAbitazione && dataInizio && dataFine) {
      checkDisponibilita();
    }
  }, [selectedAbitazione, dataInizio, dataFine]);
  
  const checkDisponibilita = async () => {
    // Chiamata API per verificare sovrapposizioni
  };
  
  // Calcolo automatico prezzo totale
  const calcolaPrezzo = () => {
    if (!selectedAbitazione || !dataInizio || !dataFine) return 0;
    const giorni = // calcola differenza giorni
    return giorni * selectedAbitazione.prezzo;
  };
}
```

#### FeedbackForm
```jsx
function FeedbackForm({ prenotazione, onSave, onCancel }) {
  const [punteggio, setPunteggio] = useState(0);
  const [titolo, setTitolo] = useState('');
  const [testo, setTesto] = useState('');
  
  // Componente stelle per punteggio
  // Validazione: minimo 1 stella, massimo 5
}
```

#### ConfirmDialog
```jsx
function ConfirmDialog({ isOpen, title, message, onConfirm, onCancel }) {
  // Modale di conferma per azioni distruttive
  // Usato prima di eliminare entità
}
```

#### LoadingSpinner
```jsx
function LoadingSpinner({ size = 'medium' }) {
  // Spinner per stati di caricamento
}
```

#### ErrorMessage
```jsx
function ErrorMessage({ message, onDismiss }) {
  // Alert rosso per errori
  // Dismissibile con X
}
```

### Services (API Calls)

#### utenteService.js
```javascript
import api from './api';

export const utenteService = {
  getAll: () => api.get('/utenti'),
  getById: (id) => api.get(`/utenti/${id}`),
  create: (data) => api.post('/utenti', data),
  update: (id, data) => api.put(`/utenti/${id}`, data),
  delete: (id) => api.delete(`/utenti/${id}`),
  getUltimaPrenotazione: (id) => api.get(`/utenti/${id}/ultima-prenotazione`)
};
```

#### hostService.js
```javascript
export const hostService = {
  getAll: () => api.get('/host'),
  getSuperHost: () => api.get('/host/super-host'),
  promuovi: (data) => api.post('/host', data),
  getAbitazioni: (codiceHost) => api.get(`/host/${codiceHost}/abitazioni`)
};
```

#### api.js
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor per gestione errori globale
api.interceptors.response.use(
  response => response,
  error => {
    // Logging errore
    console.error('API Error:', error.response?.data);
    return Promise.reject(error);
  }
);

export default api;
```

### Custom Hooks

#### useApi.js
```javascript
import { useState, useEffect } from 'react';

export function useApi(apiFunction, dependencies = []) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const response = await apiFunction();
        setData(response.data);
        setError(null);
      } catch (err) {
        setError(err.response?.data?.message || 'Errore sconosciuto');
      } finally {
        setLoading(false);
      }
    };
    
    fetchData();
  }, dependencies);
  
  return { data, loading, error };
}
```

#### useForm.js
```javascript
import { useState } from 'react';

export function useForm(initialValues, onSubmit) {
  const [values, setValues] = useState(initialValues);
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setValues(prev => ({ ...prev, [name]: value }));
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await onSubmit(values);
    } catch (error) {
      setErrors(error.response?.data?.details || {});
    } finally {
      setSubmitting(false);
    }
  };
  
  return {
    values,
    errors,
    submitting,
    handleChange,
    handleSubmit,
    setValues
  };
}
```

---

## Database

### Script DDL (schema.sql)

```sql
-- Creazione database
CREATE DATABASE IF NOT EXISTS turista_facoltoso;

-- Connessione al database (PostgreSQL)
\c turista_facoltoso;

-- Users Table
CREATE TABLE Users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    address VARCHAR(255),
    registration_date DATE
);

-- Hosts Table
CREATE TABLE Hosts (
    user_id INT PRIMARY KEY REFERENCES Users(id),
    host_code VARCHAR(50) UNIQUE NOT NULL,
    total_bookings INT DEFAULT 0,
    registration_date DATE,
    is_super_host BOOLEAN DEFAULT FALSE
);

-- Residences Table
CREATE TABLE Residences (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    number_of_rooms INT,
    number_of_beds INT,
    floor INT,
    price DECIMAL(10,2),
    availability_start DATE,
    availability_end DATE,
    host_id INT NOT NULL REFERENCES Hosts(user_id)
);

-- Bookings Table
CREATE TABLE Bookings (
    id SERIAL PRIMARY KEY,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    residence_id INT REFERENCES Residences(id),
    user_id INT REFERENCES Users(id),
    CONSTRAINT chk_dates CHECK (end_date > start_date)
);

-- Feedbacks Table
CREATE TABLE Feedbacks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    text TEXT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    booking_id INT REFERENCES Bookings(id),
    user_id INT REFERENCES Users(id)
);

-- ------------------------
-- INDEXES SUGGESTED
-- ------------------------

-- USERS
CREATE INDEX idx_users_email ON Users(email);                   -- ricerca utenti per email
CREATE INDEX idx_users_registration_date ON Users(registration_date);  -- utenti recenti

-- HOSTS
CREATE INDEX idx_hosts_host_code ON Hosts(host_code);          -- ricerca host per codice
CREATE INDEX idx_hosts_total_bookings ON Hosts(total_bookings); -- top hosts per prenotazioni
CREATE INDEX idx_hosts_is_super_host ON Hosts(is_super_host); -- filtri super host

-- RESIDENCES
CREATE INDEX idx_residences_host_id ON Residences(host_id);       -- join host-residences
CREATE INDEX idx_residences_price ON Residences(price);           -- filtri per prezzo
CREATE INDEX idx_residences_availability_start ON Residences(availability_start); -- filtraggio disponibilità
CREATE INDEX idx_residences_availability_end ON Residences(availability_end);
CREATE INDEX idx_residences_number_of_beds ON Residences(number_of_beds); -- statistiche letti
CREATE INDEX idx_residences_number_of_rooms ON Residences(number_of_rooms);

-- BOOKINGS
CREATE INDEX idx_bookings_user_id ON Bookings(user_id);           -- join user-bookings
CREATE INDEX idx_bookings_residence_id ON Bookings(residence_id); -- join residence-bookings
CREATE INDEX idx_bookings_start_date ON Bookings(start_date);     -- filtri per periodo
CREATE INDEX idx_bookings_end_date ON Bookings(end_date);
CREATE INDEX idx_bookings_user_start_date ON Bookings(user_id, start_date); -- statistiche top users
CREATE INDEX idx_bookings_residence_start_date ON Bookings(residence_id, start_date); -- statistiche top residences

-- FEEDBACKS
CREATE INDEX idx_feedbacks_booking_id ON Feedbacks(booking_id);   -- join booking-feedback
CREATE INDEX idx_feedbacks_user_id ON Feedbacks(user_id);         -- join user-feedback
CREATE INDEX idx_feedbacks_rating ON Feedbacks(rating);           -- calcolo punteggio medio
CREATE INDEX idx_feedbacks_created_rating ON Feedbacks(rating);   -- eventualmente per filtri temporali


```

### Script DML (data.sql)

```sql
-- Insert normal users
INSERT INTO Users (first_name, last_name, email, password, address) VALUES
('Mario', 'Rossi', 'mario.rossi@email.com','Password1', 'Via Roma 1, Milano'),
('Laura', 'Bianchi', 'laura.bianchi@email.com','Password2', 'Corso Italia 25, Roma'),
('Paolo', 'Verdi', 'paolo.verdi@email.com', 'Password3','Via Garibaldi 10, Napoli'),
('Giulia', 'Neri', 'giulia.neri@email.com','Password4', 'Piazza Duomo 5, Firenze'),
('Andrea', 'Blu', 'andrea.blu@email.com','Password5', 'Via Torino 15, Bologna'),
('Francesca', 'Gialli', 'francesca.gialli@email.com','Password6', 'Via Dante 8, Genova'),
('Luca', 'Viola', 'luca.viola@email.com','Password7', 'Corso Vittorio 12, Torino'),
('Sara', 'Arancio', 'sara.arancio@email.com','Password8', 'Via Manzoni 20, Palermo');

-- Insert users that will become hosts
INSERT INTO Users (first_name, last_name, email, password, address) VALUES
('Giovanni', 'Ferrari', 'giovanni.ferrari@email.com','Password9', 'Via Venezia 20, Venezia'),
('Marco', 'Colombo', 'marco.colombo@email.com','Password10', 'Via Milano 8, Como'),
('Elena', 'Ricci', 'elena.ricci@email.com','Password11', 'Via Napoli 12, Amalfi'),
('Chiara', 'Russo', 'chiara.russo@email.com','Password12', 'Via Firenze 30, Siena'),
('Roberto', 'Costa', 'roberto.costa@email.com','Password3', 'Via Genova 15, Portofino');

-- Promote users to hosts
INSERT INTO Hosts (user_id, host_code) VALUES
(9, 'HOST001'),
(10, 'HOST002'),
(11, 'HOST003'),
(12, 'HOST004'),
(13, 'HOST005');

-- Insert residences
INSERT INTO Residences (name, address, number_of_rooms, number_of_beds, floor, price, availability_start, availability_end, host_id) VALUES
-- Residences HOST001 (Giovanni Ferrari)
('Villa sul Lago', 'Via del Lago 5, Como', 5, 8, 0, 250.00, '2025-01-01', '2025-12-31', 9),
('Appartamento Centro', 'Piazza San Marco 10, Venezia', 3, 4, 2, 180.00, '2025-01-01', '2025-12-31', 9),
('Loft Moderno', 'Via Garibaldi 15, Venezia', 2, 2, 3, 150.00, '2025-01-01', '2025-12-31', 9),

-- Residences HOST002 (Marco Colombo)
('Chalet in Montagna', 'Via Alpina 20, Como', 4, 6, 0, 200.00, '2025-01-01', '2025-12-31', 10),
('Casa Rustica', 'Via Provinciale 8, Bellagio', 4, 5, 0, 160.00, '2025-01-01', '2025-12-31', 10),

-- Residences HOST003 (Elena Ricci)
('Attico Panoramico', 'Via Panoramica 10, Firenze', 6, 10, 5, 300.00, '2025-01-01', '2025-12-31', 11),
('Villa con Piscina', 'Via del Mare 25, Amalfi', 7, 12, 0, 400.00, '2025-01-01', '2025-12-31', 11),
('Monolocale Elegante', 'Via Tornabuoni 5, Firenze', 1, 2, 1, 120.00, '2025-01-01', '2025-12-31', 11),

-- Residences HOST004 (Chiara Russo)
('Casale Toscano', 'Strada Provinciale 12, Siena', 8, 14, 0, 450.00, '2025-01-01', '2025-12-31', 12),
('Appartamento Storico', 'Piazza del Campo 3, Siena', 3, 4, 1, 140.00, '2025-01-01', '2025-12-31', 12),

-- Residences HOST005 (Roberto Costa)
('Villa sul Mare', 'Lungomare 50, Portofino', 6, 10, 0, 350.00, '2025-01-01', '2025-12-31', 13),
('Mansarda Romantica', 'Via Castello 8, Portofino', 2, 2, 4, 180.00, '2025-01-01', '2025-12-31', 13);

-- Insert bookings (January 2025)
INSERT INTO Bookings (start_date, end_date, residence_id, user_id) VALUES
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

-- Insert feedbacks
INSERT INTO Feedbacks (title, text, rating, booking_id, user_id) VALUES
('Fantastic stay', 'Beautiful villa with amazing lake view. Very kind and welcoming host.', 5, 1, 1),
('Great location', 'Apartment in the city center, very convenient for visiting Venice. Clean and well-furnished.', 4, 2, 2),
('Top experience', 'Perfect chalet for a week in the snow. Everything as described.', 5, 3, 3),
('Highly recommended', 'Spacious and bright penthouse with incredible view of Florence. We will return for sure.', 5, 4, 4),
('Wonderful', 'The villa is even more beautiful in real life. Fantastic pool and well-kept garden.', 5, 5, 5),
('Very cozy', 'Modern loft, exactly like in the photos. Only downside: a bit noisy.', 4, 6, 6);

-- Most booked residence in the last month
SELECT r.id, r.name, COUNT(b.id) AS total_bookings
FROM Residences r
JOIN Bookings b ON r.id = b.residence_id
WHERE b.start_date >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY r.id, r.name
ORDER BY total_bookings DESC
LIMIT 1;

-- Hosts with most bookings in the last month
SELECT h.user_id, u.first_name, u.last_name, h.host_code, COUNT(b.id) AS total_bookings
FROM Hosts h
JOIN Users u ON h.user_id = u.id
JOIN Residences r ON r.host_id = h.user_id
JOIN Bookings b ON b.residence_id = r.id
WHERE b.start_date >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY h.user_id, u.first_name, u.last_name, h.host_code
ORDER BY total_bookings DESC;

-- Top 5 users with most booked days in the last month
SELECT u.id, u.first_name, u.last_name, 
       SUM(b.end_date - b.start_date) AS total_days
FROM Users u
JOIN Bookings b ON u.id = b.user_id
WHERE b.start_date >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY u.id, u.first_name, u.last_name
ORDER BY total_days DESC
LIMIT 5;

-- Average number of beds
SELECT AVG(number_of_beds) AS avg_beds
FROM Residences;

-- Overlapping bookings check
SELECT b1.*
FROM Bookings b1
JOIN Bookings b2 ON b1.residence_id = b2.residence_id
    AND b1.id != b2.id
WHERE (b1.start_date BETWEEN b2.start_date AND b2.end_date)
   OR (b1.end_date BETWEEN b2.start_date AND b2.end_date)
   OR (b2.start_date BETWEEN b1.start_date AND b1.end_date);

-- Last booking for a user
SELECT DISTINCT ON (user_id) *
FROM Bookings
WHERE user_id = ?
ORDER BY user_id, created_at DESC;

-- Residences of a specific host
SELECT r.*, 
       COUNT(DISTINCT b.id) AS total_bookings
FROM Residences r
LEFT JOIN Bookings b ON r.id = b.residence_id
WHERE r.host_id = (SELECT user_id FROM Hosts WHERE host_code = ?)
GROUP BY r.id;

-- Average rating per host
SELECT h.user_id, u.first_name, u.last_name,
       AVG(f.rating) AS avg_rating,
       COUNT(f.id) AS total_feedbacks
FROM Hosts h
JOIN Users u ON h.user_id = u.id
JOIN Residences r ON r.host_id = h.user_id
JOIN Bookings b ON b.residence_id = r.id
LEFT JOIN Feedbacks f ON f.booking_id = b.id
GROUP BY h.user_id, u.first_name, u.last_name;

-- Check if a user can leave feedback (has stayed?)
SELECT COUNT(*)
FROM Bookings b
WHERE b.user_id = ?
  AND b.residence_id IN (
    SELECT id FROM Residences WHERE host_id = ?
  )
  AND b.end_date < CURRENT_DATE;

```

---

## Piano di Sviluppo

### Fase 1: Setup Iniziale (Giorno 1)
- [ ] Creazione repository GitHub
- [ ] Setup progetto backend (Spring Boot / Java puro)
- [ ] Setup progetto frontend (React con Vite)
- [ ] Creazione database PostgreSQL
- [ ] Esecuzione script DDL e DML
- [ ] Configurazione connessione database
- [ ] Primo commit: struttura base

### Fase 2: Backend - Model e DAO (Giorni 2-3)
- [ ] Implementazione classi model (Utente, Host, SuperHost, Abitazione, Prenotazione, Feedback)
- [ ] Implementazione DAO per tutte le entità
- [ ] Test connessione database
- [ ] Implementazione query base (CRUD)
- [ ] Logging con SLF4J/Log4j
- [ ] Commit: layer DAO completo

### Fase 3: Backend - Service e Controller (Giorni 4-5)
- [ ] Implementazione Service layer
- [ ] Validazioni business logic
- [ ] Implementazione eccezioni personalizzate
- [ ] Implementazione Controller REST
- [ ] Test API con Postman
- [ ] Commit: API REST funzionanti

### Fase 4: Backend - Statistiche (Giorno 6)
- [ ] Implementazione query statistiche
- [ ] Service per statistiche
- [ ] Controller statistiche
- [ ] Test query complesse
- [ ] Commit: statistiche complete

### Fase 5: Frontend - Setup e Componenti Base (Giorni 7-8)
- [ ] Setup routing (React Router)
- [ ] Componenti comuni (Navbar, Footer, Loading, Error)
- [ ] Services per chiamate API
- [ ] Custom hooks (useApi, useForm)
- [ ] Commit: struttura frontend base

### Fase 6: Frontend - Pagine CRUD (Giorni 9-11)
- [ ] UtentiPage (lista, form, eliminazione)
- [ ] AbitazioniPage
- [ ] PrenotazioniPage
- [ ] FeedbackPage
- [ ] HostPage
- [ ] Commit: CRUD completo

### Fase 7: Frontend - Statistiche e Dashboard (Giorno 12)
- [ ] StatistichePage con grafici
- [ ] HomePage dashboard
- [ ] Integrazione Chart.js/Recharts
- [ ] Commit: UI completa

### Fase 8: Testing e Refinement (Giorno 13)
- [ ] Test end-to-end tutte le funzionalità
- [ ] Fix bug
- [ ] Validazioni form
- [ ] Gestione errori
- [ ] Commit: bug fix

### Fase 9: Documentazione e Deploy (Giorno 14)
- [ ] README.md completo
- [ ] Commenti JavaDoc
- [ ] Istruzioni setup locale
- [ ] Screenshot applicazione
- [ ] Commit finale
- [ ] Push su GitHub

### Checklist Pre-Consegna
- [ ] Tutti i requisiti funzionali implementati
- [ ] Database con script DDL e DML
- [ ] API REST funzionanti e testate
- [ ] Frontend responsive
- [ ] Codice commentato e pulito
- [ ] Repository GitHub pubblico
- [ ] README con istruzioni chiare
- [ ] Commit significativi e frequenti
- [ ] Nessun file sensibile (password, chiavi)

### Preparazione Presentazione
- [ ] Demo delle funzionalità principali
- [ ] Spiegazione architettura
- [ ] Descrizione scelte tecniche
- [ ] Evidenziare punti di forza
- [ ] Prepararsi a domande su:
  - Pattern utilizzati (DAO, Service, DTO)
  - Gestione eccezioni
  - Query SQL complesse
  - React hooks e gestione stato
  - Validazioni e sicurezza

---

**Documento versione**: 1.0  
**Ultimo aggiornamento**: Febbraio 2026  
**Autore**: Giuseppe Tesse