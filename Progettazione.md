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
│   ├── java/
│   │   └── com/turistafacoltoso/
│   │       ├── TuristaFacoltosoApplication.java
│   │       ├── model/
│   │       │   ├── Utente.java
│   │       │   ├── Host.java
│   │       │   ├── Abitazione.java
│   │       │   ├── Prenotazione.java
│   │       │   └── Feedback.java
│   │       ├── dao/
│   │       │   ├── UtenteDAO.java
│   │       │   ├── HostDAO.java
│   │       │   ├── AbitazioneDAO.java
│   │       │   ├── PrenotazioneDAO.java
│   │       │   └── FeedbackDAO.java
|   |       ├── router/
|   |       |   └── Router.java
│   │       ├── service/
│   │       │   ├── UtenteService.java
│   │       │   ├── HostService.java
│   │       │   ├── AbitazioneService.java
│   │       │   ├── PrenotazioneService.java
│   │       │   ├── FeedbackService.java
│   │       │   └── StatisticheService.java
│   │       ├── controller/
│   │       │   ├── UtenteController.java
│   │       │   ├── HostController.java
│   │       │   ├── AbitazioneController.java
│   │       │   ├── PrenotazioneController.java
│   │       │   ├── FeedbackController.java
│   │       │   └── StatisticheController.java
│   │       ├── exception/
│   │       │   ├── ResourceNotFoundException.java
│   │       │   ├── BadRequestException.java
│   │       │   ├── DuplicateResourceException.java
│   │       │   └── GlobalExceptionHandler.java
│   │       └── util/
│   │           ├── DatabaseConnection.java
│   │           └── DateUtils.java
│   └── resources/
│       ├── application.properties
│       ├── schema.sql
│       └── data.sql
```

### Pattern Utilizzati

#### 1. DAO Pattern
Separazione logica di accesso ai dati dal business logic.

```java
public interface UtenteDAO {
    Utente create(Utente utente);
    Utente findById(Long id);
    Utente findByEmail(String email);
    List<Utente> findAll();
    Utente update(Utente utente);
    void delete(Long id);
}
```

#### 2. Service Layer Pattern
Logica di business centralizzata.

```java
public class PrenotazioneService {
    private PrenotazioneDAO prenotazioneDAO;
    private AbitazioneDAO abitazioneDAO;
    
    public Prenotazione createPrenotazione(PrenotazioneDTO dto) {
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
    UTENTE {
        Long id PK
        string nome
        string cognome
        string email
        string password
        string indirizzo
        date data_registrazione
    }

    HOST {
        Long utente_id PK, FK
        string codice_host
        int prenotazioni_totali
        date data_registrazione
    }

    ABITAZIONE {
        Long id PK
        string nome
        string indirizzo
        int numero_locali
        int numero_posti_letto
        int piano
        decimal prezzo
        date disponibilita_inizio
        date disponibilita_fine
        Long host_id FK
    }

    PRENOTAZIONE {
        Long id PK
        date data_inizio
        date data_fine
        Long abitazione_id FK
        Long utente_id FK
    }

    FEEDBACK {
        Long id PK
        string titolo
        string testo
        int punteggio
        Long prenotazione_id FK
        Long utente_id FK
    }

    UTENTE ||--o{ PRENOTAZIONE : effettua
    ABITAZIONE ||--o{ PRENOTAZIONE : viene_prenotata
    HOST ||--o{ ABITAZIONE : carica
    PRENOTAZIONE ||--o{ FEEDBACK : genera
    UTENTE ||--o{ FEEDBACK : lascia

    UTENTE ||--|| HOST : "is a"
    HOST ||--|| SUPER_HOST : "is a"
```

### Descrizione Entità

#### UTENTE
- **id**: SERIAL, chiave primaria
- **nome**: VARCHAR(100), NOT NULL
- **cognome**: VARCHAR(100), NOT NULL
- **email**: VARCHAR(150), UNIQUE, NOT NULL
- **indirizzo**: VARCHAR(255), nullable

**Relazioni**: 
- Un utente può diventare host (1:1 opzionale con HOST)
- Un utente può effettuare molte prenotazioni (1:N con PRENOTAZIONE)
- Un utente può lasciare molti feedback (1:N con FEEDBACK)

#### HOST
- **utente_id**: INT, chiave primaria e foreign key → UTENTE(id)
- **codice_host**: VARCHAR(50), UNIQUE, NOT NULL
- **prenotazioni_totali**: INT, DEFAULT 0
- **data_registrazione**: DATE
- **isSuperHost**: BOOLEAN DEFAULT False

**Relazioni**:
- Un host è anche un utente (1:1 con UTENTE)
- Un host può avere molte abitazioni (1:N con ABITAZIONE)
- Un host può diventare super-host (1:1 opzionale con SUPER_HOST)

**Note**: Questa è una tabella di specializzazione. Un record esiste solo se l'utente è un host.


#### ABITAZIONE
- **id**: SERIAL, chiave primaria
- **nome**: VARCHAR(100), NOT NULL
- **indirizzo**: VARCHAR(255), NOT NULL
- **numero_locali**: INT
- **numero_posti_letto**: INT
- **piano**: INT
- **prezzo**: DECIMAL(8,2)
- **disponibilita_inizio**: DATE
- **disponibilita_fine**: DATE
- **host_id**: INT, FK → HOST(utente_id), NOT NULL

**Relazioni**:
- Un'abitazione appartiene a un solo host (N:1 con HOST)
- Un'abitazione può avere molte prenotazioni (1:N con PRENOTAZIONE)

#### PRENOTAZIONE
- **id**: SERIAL, chiave primaria
- **data_inizio**: DATE, NOT NULL
- **data_fine**: DATE, NOT NULL
- **abitazione_id**: INT, FK → ABITAZIONE(id)
- **utente_id**: INT, FK → UTENTE(id)

**Relazioni**:
- Una prenotazione riguarda un'abitazione (N:1 con ABITAZIONE)
- Una prenotazione è effettuata da un utente (N:1 con UTENTE)
- Una prenotazione può avere un feedback (1:1 opzionale con FEEDBACK)

**Vincoli**:
- data_fine deve essere successiva a data_inizio
- Non possono esistere prenotazioni sovrapposte per la stessa abitazione

#### FEEDBACK
- **id**: SERIAL, chiave primaria
- **titolo**: VARCHAR(100)
- **testo**: TEXT
- **punteggio**: INT, CHECK (punteggio BETWEEN 1 AND 5)
- **prenotazione_id**: INT, FK → PRENOTAZIONE(id)
- **utente_id**: INT, FK → UTENTE(id)

**Relazioni**:
- Un feedback è collegato a una prenotazione (N:1 con PRENOTAZIONE)
- Un feedback è lasciato da un utente (N:1 con UTENTE)

**Note**: Il feedback si riferisce al proprietario (host) dell'abitazione prenotata. L'host viene identificato tramite la relazione: FEEDBACK → PRENOTAZIONE → ABITAZIONE → HOST.

---

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

#### GET /utenti
Ottiene tutti gli utenti

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "nome": "Mario",
    "cognome": "Rossi",
    "email": "mario.rossi@email.com",
    "indirizzo": "Via Roma 1, Milano",
    "isHost": false,
    "isSuperHost": false
  },
  {
    "id": 2,
    "nome": "Giovanni",
    "cognome": "Bianchi",
    "email": "giovanni.bianchi@email.com",
    "indirizzo": "Via Venezia 20, Venezia",
    "isHost": true,
    "isSuperHost": true,
    "codiceHost": "HOST001",
    "prenotazioniTotali": 150
  }
]
```

#### GET /utenti/{id}
Ottiene un utente specifico

**Response 200 OK / 404 Not Found**

#### POST /utenti
Crea un nuovo utente

**Request Body:**
```json
{
  "nome": "Mario",
  "cognome": "Rossi",
  "email": "mario.rossi@email.com",
  "indirizzo": "Via Roma 1, Milano"
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "nome": "Mario",
  "cognome": "Rossi",
  "email": "mario.rossi@email.com",
  "indirizzo": "Via Roma 1, Milano",
  "isHost": false
}
```

**Validazioni:**
- Email obbligatoria e valida
- Email univoca
- Nome e cognome obbligatori

#### PUT /utenti/{id}
Aggiorna un utente

**Request Body:** Stesso formato del POST

**Response 200 OK / 404 Not Found**

#### DELETE /utenti/{id}
Elimina un utente

**Response 204 No Content / 404 Not Found**

**Note:** L'eliminazione è in cascata (elimina anche HOST, SUPER_HOST, prenotazioni, feedback)

#### GET /utenti/{id}/ultima-prenotazione
Ottiene l'ultima prenotazione di un utente

**Response 200 OK:**
```json
{
  "id": 15,
  "dataInizio": "2025-02-10",
  "dataFine": "2025-02-15",
  "abitazione": {
    "id": 3,
    "nome": "Appartamento Vista Mare",
    "indirizzo": "Via Mare 10, Rimini"
  },
  "giorniTotali": 5,
  "prezzoTotale": 750.00
}
```

**Response 404:** Se l'utente non ha prenotazioni

---

### 2. Host

#### GET /host
Ottiene tutti gli host

**Response 200 OK:**
```json
[
  {
    "utenteId": 2,
    "nome": "Giovanni",
    "cognome": "Bianchi",
    "email": "giovanni.bianchi@email.com",
    "codiceHost": "HOST001",
    "numeroAbitazioni": 5,
    "numeroPrenotazioni": 150,
    "isSuperHost": true,
    "dataRegistrazioneSuperHost": "2024-06-15"
  }
]
```

#### POST /host
Promuove un utente a host

**Request Body:**
```json
{
  "utenteId": 1,
  "codiceHost": "HOST005"
}
```

**Response 201 Created**

**Validazioni:**
- L'utente deve esistere
- L'utente non deve essere già un host
- Il codice host deve essere univoco

#### GET /host/super-host
Ottiene tutti i super-host (≥100 prenotazioni)

**Response 200 OK:**
```json
[
  {
    "hostId": 2,
    "nome": "Giovanni Bianchi",
    "codiceHost": "HOST001",
    "prenotazioniTotali": 150,
    "dataRegistrazione": "2024-06-15",
    "numeroAbitazioni": 5
  }
]
```

#### GET /host/{codiceHost}/abitazioni
Ottiene tutte le abitazioni di un host specifico

**Response 200 OK:** Array di abitazioni (vedi sezione Abitazioni)

---

### 3. Abitazioni

#### GET /abitazioni
Ottiene tutte le abitazioni

**Query Parameters (opzionali):**
- `hostId`: filtra per ID host
- `codiceHost`: filtra per codice host
- `minPrezzo`: prezzo minimo
- `maxPrezzo`: prezzo massimo
- `minPostiLetto`: numero minimo posti letto

**Response 200 OK:**
```json
[
  {
    "id": 1,
    "nome": "Villa sul Lago",
    "indirizzo": "Via del Lago 5, Como",
    "numeroLocali": 5,
    "numeroPostiLetto": 8,
    "piano": 0,
    "prezzo": 250.00,
    "disponibilitaInizio": "2025-01-01",
    "disponibilitaFine": "2025-12-31",
    "host": {
      "id": 2,
      "nome": "Giovanni Bianchi",
      "codiceHost": "HOST001"
    }
  }
]
```

#### GET /abitazioni/{id}
Ottiene un'abitazione specifica

**Response 200 OK / 404 Not Found**

#### POST /abitazioni
Crea una nuova abitazione

**Request Body:**
```json
{
  "nome": "Villa sul Lago",
  "indirizzo": "Via del Lago 5, Como",
  "numeroLocali": 5,
  "numeroPostiLetto": 8,
  "piano": 0,
  "prezzo": 250.00,
  "disponibilitaInizio": "2025-01-01",
  "disponibilitaFine": "2025-12-31",
  "hostId": 2
}
```

**Response 201 Created**

**Validazioni:**
- L'host deve esistere nella tabella HOST
- disponibilitaFine > disponibilitaInizio
- Prezzo > 0

#### PUT /abitazioni/{id}
Aggiorna un'abitazione

**Request Body:** Stesso formato del POST

**Response 200 OK / 404 Not Found**

#### DELETE /abitazioni/{id}
Elimina un'abitazione

**Response 204 No Content / 404 Not Found**

---

### 4. Prenotazioni

#### GET /prenotazioni
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
    "dataInizio": "2025-02-01",
    "dataFine": "2025-02-05",
    "utente": {
      "id": 1,
      "nome": "Mario",
      "cognome": "Rossi"
    },
    "abitazione": {
      "id": 3,
      "nome": "Appartamento Centro",
      "indirizzo": "Via Milano 5, Roma"
    },
    "giorniTotali": 4,
    "prezzoTotale": 600.00,
    "hasFeedback": false
  }
]
```

#### GET /prenotazioni/{id}
Ottiene una prenotazione specifica

**Response 200 OK / 404 Not Found**

#### POST /prenotazioni
Crea una nuova prenotazione

**Request Body:**
```json
{
  "dataInizio": "2025-02-01",
  "dataFine": "2025-02-05",
  "utenteId": 1,
  "abitazioneId": 3
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

#### PUT /prenotazioni/{id}
Aggiorna una prenotazione

**Request Body:** Stesso formato del POST

**Response 200 OK / 404 Not Found**

**Note:** Esegue le stesse validazioni del POST

#### DELETE /prenotazioni/{id}
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
    "titolo": "Soggiorno fantastico",
    "testo": "Ottima esperienza, host disponibile e casa pulitissima",
    "punteggio": 5,
    "utente": {
      "id": 1,
      "nome": "Mario Rossi"
    },
    "prenotazione": {
      "id": 10,
      "abitazione": "Villa sul Lago"
    },
    "host": {
      "id": 2,
      "nome": "Giovanni Bianchi",
      "codiceHost": "HOST001"
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
  "titolo": "Ottimo soggiorno",
  "testo": "Casa bellissima e host molto disponibile",
  "punteggio": 5,
  "prenotazioneId": 10,
  "utenteId": 1
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
  "titolo": "Soggiorno eccellente",
  "testo": "Esperienza da ripetere assolutamente",
  "punteggio": 5
}
```

**Response 200 OK / 404 Not Found**

#### DELETE /feedback/{id}
Elimina un feedback

**Response 204 No Content / 404 Not Found**

---

### 6. Statistiche

#### GET /statistiche/abitazione-piu-gettonata
Ottiene l'abitazione più prenotata nell'ultimo mese

**Response 200 OK:**
```json
{
  "abitazione": {
    "id": 5,
    "nome": "Attico Panoramico",
    "indirizzo": "Via Panoramica 10, Firenze",
    "prezzo": 180.00
  },
  "numeroPrenotazioni": 12,
  "giorniTotaliPrenotati": 48,
  "host": {
    "nome": "Laura Verdi",
    "codiceHost": "HOST003"
  }
}
```

**Query SQL:**
```sql
SELECT a.*, COUNT(p.id) as num_prenotazioni, 
       SUM(p.data_fine - p.data_inizio) as giorni_totali
FROM abitazione a
JOIN prenotazione p ON a.id = p.abitazione_id
WHERE p.data_inizio >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY a.id
ORDER BY num_prenotazioni DESC
LIMIT 1;
```

#### GET /statistiche/host-top-prenotazioni
Ottiene gli host con più prenotazioni nell'ultimo mese

**Query Parameters (opzionali):**
- `limit`: numero massimo di risultati (default: 10)

**Response 200 OK:**
```json
[
  {
    "host": {
      "id": 3,
      "nome": "Laura",
      "cognome": "Verdi",
      "codiceHost": "HOST003"
    },
    "numeroPrenotazioni": 25,
    "giorniTotaliPrenotati": 180,
    "numeroAbitazioni": 7,
    "isSuperHost": true
  }
]
```

**Query SQL:**
```sql
SELECT h.*, u.nome, u.cognome, 
       COUNT(p.id) as num_prenotazioni,
       SUM(p.data_fine - p.data_inizio) as giorni_totali
FROM host h
JOIN utente u ON h.utente_id = u.id
JOIN abitazione a ON a.host_id = h.utente_id
JOIN prenotazione p ON p.abitazione_id = a.id
WHERE p.data_inizio >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY h.utente_id, u.nome, u.cognome
ORDER BY num_prenotazioni DESC
LIMIT 10;
```

#### GET /statistiche/utenti-top-giorni
Ottiene i 5 utenti con più giorni prenotati nell'ultimo mese

**Response 200 OK:**
```json
[
  {
    "utente": {
      "id": 7,
      "nome": "Paolo",
      "cognome": "Neri",
      "email": "paolo.neri@email.com"
    },
    "giorniPrenotati": 28,
    "numeroPrenotazioni": 4,
    "spesaTotale": 4200.00
  }
]
```

**Query SQL:**
```sql
SELECT u.*, 
       SUM(p.data_fine - p.data_inizio) as giorni_prenotati,
       COUNT(p.id) as num_prenotazioni,
       SUM((p.data_fine - p.data_inizio) * a.prezzo) as spesa_totale
FROM utente u
JOIN prenotazione p ON u.id = p.utente_id
JOIN abitazione a ON p.abitazione_id = a.id
WHERE p.data_inizio >= CURRENT_DATE - INTERVAL '1 month'
GROUP BY u.id
ORDER BY giorni_prenotati DESC
LIMIT 5;
```

#### GET /statistiche/media-posti-letto
Ottiene il numero medio di posti letto di tutte le abitazioni

**Response 200 OK:**
```json
{
  "mediaPostiLetto": 5.2,
  "totaleAbitazioni": 150,
  "minPostiLetto": 1,
  "maxPostiLetto": 12,
  "distribuzione": {
    "1-2": 25,
    "3-4": 60,
    "5-6": 40,
    "7+": 25
  }
}
```

**Query SQL:**
```sql
SELECT AVG(numero_posti_letto) as media,
       COUNT(*) as totale,
       MIN(numero_posti_letto) as minimo,
       MAX(numero_posti_letto) as massimo
FROM abitazione;
```

#### GET /statistiche/dashboard
Ottiene un riepilogo completo di tutte le statistiche

**Response 200 OK:**
```json
{
  "overview": {
    "totaleUtenti": 250,
    "totaleHost": 45,
    "totaleSuperHost": 8,
    "totaleAbitazioni": 150,
    "totalePrenotazioni": 890,
    "totaleFeedback": 420
  },
  "ultimoMese": {
    "nuovePrenotazioni": 78,
    "nuoviUtenti": 15,
    "nuoveAbitazioni": 8,
    "feedbackRicevuti": 62
  },
  "abitazionePiuGettonata": { /* ... */ },
  "topHost": [ /* ... */ ],
  "topUtenti": [ /* ... */ ],
  "mediaPostiLetto": 5.2,
  "punteggioMedioFeedback": 4.3
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
```

### Script DML (data.sql)

```sql
-- Inserimento utenti normali
INSERT INTO utente (nome, cognome, email,password, indirizzo) VALUES
('Mario', 'Rossi', 'mario.rossi@email.com','Password1', 'Via Roma 1, Milano'),
('Laura', 'Bianchi', 'laura.bianchi@email.com','Password2', 'Corso Italia 25, Roma'),
('Paolo', 'Verdi', 'paolo.verdi@email.com', 'Password3','Via Garibaldi 10, Napoli'),
('Giulia', 'Neri', 'giulia.neri@email.com','Password4', 'Piazza Duomo 5, Firenze'),
('Andrea', 'Blu', 'andrea.blu@email.com','Password5', 'Via Torino 15, Bologna'),
('Francesca', 'Gialli', 'francesca.gialli@email.com','Password6', 'Via Dante 8, Genova'),
('Luca', 'Viola', 'luca.viola@email.com','Password7', 'Corso Vittorio 12, Torino'),
('Sara', 'Arancio', 'sara.arancio@email.com','Password8', 'Via Manzoni 20, Palermo');

-- Inserimento utenti che diventeranno host
INSERT INTO utente (nome, cognome, email,password, indirizzo) VALUES
('Giovanni', 'Ferrari', 'giovanni.ferrari@email.com','Password9', 'Via Venezia 20, Venezia'),
('Marco', 'Colombo', 'marco.colombo@email.com','Password10', 'Via Milano 8, Como'),
('Elena', 'Ricci', 'elena.ricci@email.com','Password11', 'Via Napoli 12, Amalfi'),
('Chiara', 'Russo', 'chiara.russo@email.com','Password12', 'Via Firenze 30, Siena'),
('Roberto', 'Costa', 'roberto.costa@email.com','Password3', 'Via Genova 15, Portofino');

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
```

### Query Utili per le Statistiche

```sql
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

```markdown
## Note Tecniche Aggiuntive

### Validazioni Backend

#### Validazione Email
```java
public void validateEmail(String email) {
    String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
    if (!email.matches(emailRegex)) {
        throw new BadRequestException("Email non valida");
    }
}
```

#### Validazione Date Prenotazione
```java
public void validatePrenotazioneDate(Date inizio, Date fine, Abitazione abitazione) {
    // Controllo date logiche
    if (fine.before(inizio) || fine.equals(inizio)) {
        throw new BadRequestException("Data fine deve essere successiva a data inizio");
    }
    
    // Controllo disponibilità abitazione
    if (inizio.before(abitazione.getDisponibilitaInizio()) || 
        fine.after(abitazione.getDisponibilitaFine())) {
        throw new BadRequestException(
            "Le date non rientrano nel periodo di disponibilità dell'abitazione"
        );
    }
    
    // Controllo sovrapposizioni
    List<Prenotazione> prenotazioniEsistenti = 
        prenotazioneDAO.findByAbitazioneAndPeriodo(abitazione.getId(), inizio, fine);
    
    if (!prenotazioniEsistenti.isEmpty()) {
        throw new BadRequestException(
            "L'abitazione non è disponibile nel periodo selezionato"
        );
    }
}
```

#### Validazione Feedback
```java
public void validateFeedback(FeedbackDTO dto) {
    // Verifica che l'utente abbia effettivamente soggiornato
    Prenotazione prenotazione = prenotazioneDAO.findById(dto.getPrenotazioneId());
    
    if (prenotazione == null) {
        throw new ResourceNotFoundException("Prenotazione non trovata");
    }
    
    if (!prenotazione.getUtenteId().equals(dto.getUtenteId())) {
        throw new BadRequestException(
            "Solo l'utente che ha effettuato la prenotazione può lasciare un feedback"
        );
    }
    
    // Verifica che la prenotazione sia terminata
    if (prenotazione.getDataFine().after(new Date())) {
        throw new BadRequestException(
            "Non puoi lasciare un feedback prima della fine del soggiorno"
        );
    }
    
    // Verifica che non esista già un feedback per questa prenotazione
    if (feedbackDAO.existsByPrenotazioneId(dto.getPrenotazioneId())) {
        throw new BadRequestException(
            "Esiste già un feedback per questa prenotazione"
        );
    }
}
```

### Gestione Transazioni

#### Creazione Prenotazione con Aggiornamento Super-Host
```java
@Transactional
public Prenotazione createPrenotazione(PrenotazioneDTO dto) throws SQLException {
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        
        // 1. Crea la prenotazione
        Prenotazione prenotazione = prenotazioneDAO.create(dto.toEntity(), conn);
        
        // 2. Recupera l'host dell'abitazione
        Abitazione abitazione = abitazioneDAO.findById(dto.getAbitazioneId());
        int hostId = abitazione.getHostId();
        
        // 3. Incrementa il contatore prenotazioni dell'host
        hostDAO.incrementaPrenotazioni(hostId, conn);
        
        // 4. Verifica se l'host ha raggiunto 100 prenotazioni
        Host host = hostDAO.findById(hostId);
        if (host.getPrenotazioniTotali() >= 100) {
            // 5. Se non è già super-host, promuovilo
            if (!superHostDAO.exists(hostId)) {
                SuperHost superHost = new SuperHost();
                superHost.setHostId(hostId);
                superHost.setPrenotazioniTotali(host.getPrenotazioniTotali());
                superHost.setDataRegistrazione(new Date());
                superHostDAO.create(superHost, conn);
                
                logger.info("Host {} promosso a Super-Host!", hostId);
            }
        }
        
        conn.commit();
        return prenotazione;
        
    } catch (Exception e) {
        if (conn != null) {
            conn.rollback();
        }
        logger.error("Errore nella creazione della prenotazione", e);
        throw e;
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
```

#### Eliminazione Prenotazione con Decremento Contatore
```java
@Transactional
public void deletePrenotazione(Long id) throws SQLException {
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        
        // 1. Recupera la prenotazione prima di eliminarla
        Prenotazione prenotazione = prenotazioneDAO.findById(id);
        if (prenotazione == null) {
            throw new ResourceNotFoundException("Prenotazione non trovata");
        }
        
        // 2. Recupera l'host
        Abitazione abitazione = abitazioneDAO.findById(prenotazione.getAbitazioneId());
        int hostId = abitazione.getHostId();
        
        // 3. Elimina la prenotazione (il feedback viene eliminato in cascata)
        prenotazioneDAO.delete(id, conn);
        
        // 4. Decrementa il contatore prenotazioni
        hostDAO.decrementaPrenotazioni(hostId, conn);
        
        // 5. Verifica se il super-host scende sotto 100 prenotazioni
        Host host = hostDAO.findById(hostId);
        if (host.getPrenotazioniTotali() < 100) {
            superHostDAO.delete(hostId, conn);
            logger.info("Host {} non è più Super-Host", hostId);
        }
        
        conn.commit();
        
    } catch (Exception e) {
        if (conn != null) {
            conn.rollback();
        }
        throw e;
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
```

### CORS Configuration (Spring Boot)

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permetti richieste da React dev server
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
        
        // Permetti tutti i metodi HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Permetti tutti gli header
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Permetti credenziali
        config.setAllowCredentials(true);
        
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
```

### Logging Configuration

#### logback.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Console Appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- File Appender -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/turista-facoltoso.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/turista-facoltoso.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Logger per il package del progetto -->
    <logger name="com.turistafacoltoso" level="DEBUG" />
    
    <!-- Logger per SQL (opzionale, utile per debug)
    <logger name="java.sql" level="DEBUG" />
    <logger name="org.hibernate.SQL" level="DEBUG" />
    -->
    
    <!-- Root logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

#### Utilizzo nei Service
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrenotazioneService {
    private static final Logger logger = LoggerFactory.getLogger(PrenotazioneService.class);
    
    public Prenotazione create(PrenotazioneDTO dto) {
        logger.debug("Creazione prenotazione per utente {}, abitazione {}", 
                     dto.getUtenteId(), dto.getAbitazioneId());
        
        try {
            // Validazioni
            validatePrenotazione(dto);
            logger.debug("Validazione prenotazione completata");
            
            // Creazione
            Prenotazione prenotazione = prenotazioneDAO.create(dto.toEntity());
            logger.info("Prenotazione {} creata con successo", prenotazione.getId());
            
            return prenotazione;
        } catch (Exception e) {
            logger.error("Errore nella creazione della prenotazione", e);
            throw e;
        }
    }
}
```

### Connection Pooling (Opzionale ma consigliato)

```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
    private static HikariDataSource dataSource;
    
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/turista_facoltoso");
        config.setUsername("postgres");
        config.setPassword("password");
        
        // Configurazione pool
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        dataSource = new HikariDataSource(config);
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
```

### Exception Handling Globale

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warn("Risorsa non trovata: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            null
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        logger.warn("Richiesta non valida: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            ex.getDetails()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        logger.warn("Risorsa duplicata: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            null
        );
        
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Errore interno del server", ex);
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Si è verificato un errore interno. Riprova più tardi.",
            null
        );
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### Utility per Date

```java
public class DateUtils {
    
    /**
     * Calcola il numero di giorni tra due date
     */
    public static long calcolaGiorni(Date inizio, Date fine) {
        long diffInMillies = Math.abs(fine.getTime() - inizio.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Verifica se due periodi si sovrappongono
     */
    public static boolean periodiSiSovrappongono(
            Date inizio1, Date fine1, 
            Date inizio2, Date fine2) {
        
        return (inizio1.before(fine2) || inizio1.equals(fine2)) && 
               (fine1.after(inizio2) || fine1.equals(inizio2));
    }
    
    /**
     * Verifica se una data è nell'ultimo mese
     */
    public static boolean isUltimoMese(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date unMeseFa = cal.getTime();
        return data.after(unMeseFa);
    }
    
    /**
     * Converte String a Date
     */
    public static Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(dateString);
    }
    
    /**
     * Converte Date a String
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
```

### Constants

```java
public class Constants {
    // Soglia per diventare super-host
    public static final int SOGLIA_SUPER_HOST = 100;
    
    // Punteggio minimo e massimo feedback
    public static final int MIN_PUNTEGGIO = 1;
    public static final int MAX_PUNTEGGIO = 5;
    
    // Limiti per query statistiche
    public static final int DEFAULT_LIMIT_STATISTICHE = 10;
    public static final int MAX_LIMIT_STATISTICHE = 50;
    
    // Messaggi di errore comuni
    public static final String UTENTE_NON_TROVATO = "Utente non trovato";
    public static final String HOST_NON_TROVATO = "Host non trovato";
    public static final String ABITAZIONE_NON_TROVATA = "Abitazione non trovata";
    public static final String PRENOTAZIONE_NON_TROVATA = "Prenotazione non trovata";
    
    // Regex
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
}
```

### DTO Mapper Pattern

```java
public class PrenotazioneMapper {
    
    public static PrenotazioneDTO toDTO(Prenotazione prenotazione, 
                                        Utente utente, 
                                        Abitazione abitazione) {
        PrenotazioneDTO dto = new PrenotazioneDTO();
        dto.setId(prenotazione.getId());
        dto.setDataInizio(prenotazione.getDataInizio());
        dto.setDataFine(prenotazione.getDataFine());
        
        // Mapping utente
        UtenteDTO utenteDTO = new UtenteDTO();
        utenteDTO.setId(utente.getId());
        utenteDTO.setNome(utente.getNome());
        utenteDTO.setCognome(utente.getCognome());
        dto.setUtente(utenteDTO);
        
        // Mapping abitazione
        AbitazioneDTO abitazioneDTO = new AbitazioneDTO();
        abitazioneDTO.setId(abitazione.getId());
        abitazioneDTO.setNome(abitazione.getNome());
        abitazioneDTO.setPrezzo(abitazione.getPrezzo());
        dto.setAbitazione(abitazioneDTO);
        
        // Calcoli
        long giorni = DateUtils.calcolaGiorni(
            prenotazione.getDataInizio(), 
            prenotazione.getDataFine()
        );
        dto.setGiorniTotali(giorni);
        dto.setPrezzoTotale(giorni * abitazione.getPrezzo());
        
        return dto;
    }
    
    public static Prenotazione toEntity(PrenotazioneDTO dto) {
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setId(dto.getId());
        prenotazione.setDataInizio(dto.getDataInizio());
        prenotazione.setDataFine(dto.getDataFine());
        prenotazione.setUtenteId(dto.getUtenteId());
        prenotazione.setAbitazioneId(dto.getAbitazioneId());
        return prenotazione;
    }
}
```

### Frontend - Axios Interceptors

```javascript
// src/services/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000 // 10 secondi
});

// Request interceptor - per logging o aggiunta token
api.interceptors.request.use(
  config => {
    console.log(`[API] ${config.method.toUpperCase()} ${config.url}`);
    return config;
  },
  error => {
    console.error('[API] Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor - per gestione errori globale
api.interceptors.response.use(
  response => {
    console.log(`[API] Response:`, response.data);
    return response;
  },
  error => {
    if (error.response) {
      // Errore dal server
      const { status, data } = error.response;
      console.error(`[API] Error ${status}:`, data.message);
      
      // Gestione errori specifici
      switch (status) {
        case 404:
          console.error('Risorsa non trovata');
          break;
        case 409:
          console.error('Conflitto:', data.message);
          break;
        case 500:
          console.error('Errore del server');
          break;
      }
    } else if (error.request) {
      // Nessuna risposta dal server
      console.error('[API] Nessuna risposta dal server');
    } else {
      // Errore nella configurazione
      console.error('[API] Errore:', error.message);
    }
    
    return Promise.reject(error);
  }
);

export default api;
```

### Frontend - Form Validation Utils

```javascript
// src/utils/validation.js

export const validateEmail = (email) => {
  const regex = /^[A-Za-z0-9+_.-]+@(.+)$/;
  return regex.test(email);
};

export const validateRequired = (value) => {
  return value !== null && value !== undefined && value !== '';
};

export const validateNumber = (value, min = null, max = null) => {
  const num = Number(value);
  if (isNaN(num)) return false;
  if (min !== null && num < min) return false;
  if (max !== null && num > max) return false;
  return true;
};

export const validateDate = (dateString) => {
  const date = new Date(dateString);
  return date instanceof Date && !isNaN(date);
};

export const validateDateRange = (startDate, endDate) => {
  const start = new Date(startDate);
  const end = new Date(endDate);
  return end > start;
};

export const validatePunteggio = (punteggio) => {
  return validateNumber(punteggio, 1, 5);
};

// Validazione form utente
export const validateUtenteForm = (formData) => {
  const errors = {};
  
  if (!validateRequired(formData.nome)) {
    errors.nome = 'Nome obbligatorio';
  }
  
  if (!validateRequired(formData.cognome)) {
    errors.cognome = 'Cognome obbligatorio';
  }
  
  if (!validateRequired(formData.email)) {
    errors.email = 'Email obbligatoria';
  } else if (!validateEmail(formData.email)) {
    errors.email = 'Email non valida';
  }
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};

// Validazione form prenotazione
export const validatePrenotazioneForm = (formData) => {
  const errors = {};
  
  if (!validateRequired(formData.utenteId)) {
    errors.utenteId = 'Seleziona un utente';
  }
  
  if (!validateRequired(formData.abitazioneId)) {
    errors.abitazioneId = 'Seleziona un\'abitazione';
  }
  
  if (!validateRequired(formData.dataInizio)) {
    errors.dataInizio = 'Data inizio obbligatoria';
  }
  
  if (!validateRequired(formData.dataFine)) {
    errors.dataFine = 'Data fine obbligatoria';
  }
  
  if (formData.dataInizio && formData.dataFine) {
    if (!validateDateRange(formData.dataInizio, formData.dataFine)) {
      errors.dataFine = 'La data fine deve essere successiva alla data inizio';
    }
  }
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};
```

### Frontend - Formatters

```javascript
// src/utils/formatters.js

export const formatDate = (date) => {
  if (!date) return '';
  const d = new Date(date);
  return d.toLocaleDateString('it-IT');
};

export const formatCurrency = (amount) => {
  return new Intl.NumberFormat('it-IT', {
    style: 'currency',
    currency: 'EUR'
  }).format(amount);
};

export const formatNumber = (num) => {
  return new Intl.NumberFormat('it-IT').format(num);
};

export const calcola Giorni = (dataInizio, dataFine) => {
  const start = new Date(dataInizio);
  const end = new Date(dataFine);
  const diffTime = Math.abs(end - start);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return diffDays;
};

export const renderStars = (punteggio) => {
  return '★'.repeat(punteggio) + '☆'.repeat(5 - punteggio);
};
```

---
