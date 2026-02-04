# Luxury Tourist API - Backend

API REST per la gestione di una piattaforma di affitti turistici di lusso. Costruita con Java, Maven, PostgreSQL e architettura a livelli.

## 🚀 Quick Start

```bash
# Clone del repository
git clone <repository-url>
cd luxury-tourist-backend

# Configurazione database
cp src/main/resources/application.properties
# Modifica application.properties con le tue credenziali PostgreSQL

# Build del progetto
mvn clean install

# Run dell'applicazione
mvn exec:java -Dexec.mainClass="com.giuseppe_tesse.turista.LuxuryTouristApplication"
```

L'API sarà disponibile su `http://localhost:8080`

## 📋 Prerequisiti

- **Java**: 17+
- **Maven**: 3.8+
- **PostgreSQL**: 14+
- **Postman** (per testare l'API)

## 🗄️ Database Setup

### 1. Creazione Database

```sql
-- Connettiti a PostgreSQL
psql -U postgres

-- Crea il database
CREATE DATABASE luxury_tourist;

-- Crea l'utente (opzionale)
CREATE USER tourist_user WITH PASSWORD 'tourist_pass';
GRANT ALL PRIVILEGES ON DATABASE luxury_tourist TO tourist_user;
```

### 2. Schema Automatico

Lo schema viene creato automaticamente all'avvio tramite `schema.sql`:

```sql
-- Users table
CREATE TABLE IF NOT EXISTS Users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    registration_date DATE DEFAULT CURRENT_DATE
);

-- Hosts table (inherits from Users)
CREATE TABLE IF NOT EXISTS Hosts (
    user_id SERIAL PRIMARY KEY REFERENCES Users(id) ON DELETE CASCADE,
    host_code VARCHAR(50) UNIQUE NOT NULL,
    total_bookings INT DEFAULT 0,
    registration_date DATE DEFAULT CURRENT_DATE
);

-- Residences table
CREATE TABLE IF NOT EXISTS Residences (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    number_of_rooms INT NOT NULL,
    number_of_beds INT NOT NULL,
    floor INT,
    price DECIMAL(10,2) NOT NULL,
    availability_start DATE,
    availability_end DATE,
    host_id BIGINT REFERENCES Hosts(user_id) ON DELETE CASCADE
);

-- Bookings table
CREATE TABLE IF NOT EXISTS Bookings (
    id SERIAL PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    residence_id INT REFERENCES Residences(id) ON DELETE CASCADE,
    user_id INT REFERENCES Users(id) ON DELETE CASCADE
);

-- Feedbacks table
CREATE TABLE IF NOT EXISTS Feedbacks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    text TEXT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    booking_id INT REFERENCES Bookings(id) ON DELETE CASCADE,
    user_id INT REFERENCES Users(id) ON DELETE CASCADE
);
```

## ⚙️ Configurazione

### application.properties

```properties
# Database Configuration
db.url=jdbc:postgresql://localhost:5432/luxury_tourist
db.username=tourist_user
db.password=tourist_pass
db.driver=org.postgresql.Driver

# Server Configuration
server.port=8080
server.context.path=/api/v1

# Connection Pool
db.pool.minIdle=5
db.pool.maxIdle=10
db.pool.maxTotal=20

# Logging
logging.level=INFO
logging.file=logs/luxury-tourist.log
```

## 📁 Struttura del Progetto

```
src/
├── main/
│   ├── java/
│   │   └── com/giuseppe_tesse/turista/
│   │       ├── LuxuryTouristApplication.java    # Entry point
│   │       │
│   │       ├── model/                           # Entity models (POJO)
│   │       │   ├── User.java
│   │       │   ├── Host.java
│   │       │   ├── Residence.java
│   │       │   ├── Booking.java
│   │       │   └── Feedback.java
│   │       │
│   │       ├── dao/                             # Data Access Objects
│   │       │   ├── UserDAO.java
│   │       │   ├── HostDAO.java
│   │       │   ├── ResidenceDAO.java
│   │       │   ├── BookingDAO.java
│   │       │   └── FeedbackDAO.java
│   │       │
│   │       ├── dto/                             # Data Transfer Objects
│   │       │   ├── request/
│   │       │   │   ├── UserRequestDTO.java
│   │       │   │   ├── HostRequestDTO.java
│   │       │   │   ├── ResidenceRequestDTO.java
│   │       │   │   ├── BookingRequestDTO.java
│   │       │   │   └── FeedbackRequestDTO.java
│   │       │   └── response/
│   │       │       ├── UserResponseDTO.java
│   │       │       ├── HostResponseDTO.java
│   │       │       ├── ResidenceResponseDTO.java
│   │       │       ├── BookingResponseDTO.java
│   │       │       └── FeedbackResponseDTO.java
│   │       │
│   │       ├── service/                         # Business logic
│   │       │   ├── UserService.java
│   │       │   ├── HostService.java
│   │       │   ├── ResidenceService.java
│   │       │   ├── BookingService.java
│   │       │   ├── FeedbackService.java
│   │       │   └── StatisticsService.java
│   │       │
│   │       ├── controller/                      # HTTP Controllers
│   │       │   ├── UserController.java
│   │       │   ├── HostController.java
│   │       │   ├── ResidenceController.java
│   │       │   ├── BookingController.java
│   │       │   ├── FeedbackController.java
│   │       │   └── StatisticsController.java
│   │       │
│   │       ├── router/                          # Routing configuration
│   │       │   └── Router.java
│   │       │
│   │       ├── exception/                       # Custom exceptions
│   │       │   ├── ResourceNotFoundException.java
│   │       │   ├── BadRequestException.java
│   │       │   ├── DuplicateResourceException.java
│   │       │   └── GlobalExceptionHandler.java
│   │       │
│   │       └── util/                            # Utilities
│   │           ├── DatabaseConnection.java
│   │           └── DateUtils.java
│   │
│   └── resources/
│       ├── application.properties               # App configuration
│       ├── schema.sql                          # Database schema
│       └── data.sql                            # Sample data (optional)
│
└── test/                                        # Test classes
    └── java/
        └── com/giuseppe_tesse/turista/
```

## 🏗️ Architettura

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Controller Layer            │  HTTP Requests/Responses
│  (UserController, HostController)   │  JSON parsing, validation
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│          Service Layer              │  Business logic
│   (UserService, HostService)        │  Transactions, validation
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│           DAO Layer                 │  Database operations
│     (UserDAO, HostDAO)               │  CRUD, queries
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│         Database Layer              │  PostgreSQL
│          (PostgreSQL)                │  Data persistence
└─────────────────────────────────────┘
```

### Responsabilità dei Layer

#### **Model**
- POJO che rappresentano le entità del database
- Mapping 1:1 con le tabelle SQL
- Getters/Setters, constructors

#### **DAO (Data Access Object)**
- Interfaccia diretta con il database
- Metodi CRUD: `save()`, `findById()`, `update()`, `delete()`
- Query custom con JDBC

#### **DTO (Data Transfer Object)**
- **Request DTO**: Dati in ingresso dal client
- **Response DTO**: Dati in uscita verso il client
- Separazione tra rappresentazione interna ed esterna

#### **Service**
- Business logic e validazioni
- Coordinate più DAO se necessario
- Gestione transazioni

#### **Controller**
- Gestione HTTP requests
- Parsing JSON ↔ DTO
- Chiamata ai Service
- Costruzione delle risposte HTTP

## 🔌 API Endpoints

### 📊 Base URL
```
http://localhost:8080/api/v1
```

### 👤 Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/users` | Crea nuovo utente |
| `GET` | `/users` | Lista tutti gli utenti |
| `GET` | `/users/{id}` | Dettaglio utente per ID |
| `GET` | `/users/email/{email}` | Trova utente per email |
| `GET` | `/users/stats/mdb` | Utente con più giorni prenotati |
| `PUT` | `/users/{id}` | Aggiorna utente |
| `DELETE` | `/users/{id}` | Elimina utente per ID |
| `DELETE` | `/users/email/{email}` | Elimina utente per email |
| `DELETE` | `/users` | Elimina tutti gli utenti |

**Request Body Example (POST/PUT)**:
```json
{
  "firstName": "Giuseppe",
  "lastName": "Tesse",
  "email": "giuseppe@example.com",
  "password": "secret123",
  "address": "Via Roma 10, Bari"
}
```

---

### 🏠 Hosts

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/hosts/{userId}` | Promuovi utente a Host |
| `GET` | `/hosts` | Lista tutti gli host |
| `GET` | `/super_hosts` | Lista tutti i super host |
| `GET` | `/hosts/{id}` | Dettaglio host per ID |
| `GET` | `/stats/hosts` | Top host per prenotazioni |

**Note**: Un Host è un utente promosso. Il sistema genera automaticamente un `host_code` univoco.

---

### 🏡 Residences

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/residence` | Crea nuova residenza |
| `GET` | `/residences` | Lista tutte le residenze |
| `GET` | `/residences/{id}` | Dettaglio residenza per ID |
| `GET` | `/residences/address/{address}/floor/{floor}` | Trova per indirizzo e piano |
| `GET` | `/residences/owner/{hostId}` | Residenze per host ID |
| `GET` | `/residences/owner/host_code/{code}` | Residenze per host code |
| `GET` | `/residences/stats/mprlm` | Residenza più popolare ultimo mese |
| `GET` | `/residences/stats/avg` | Media posti letto |
| `PUT` | `/residences/{id}` | Aggiorna residenza |
| `DELETE` | `/residences/{id}` | Elimina residenza per ID |
| `DELETE` | `/residences/owner/{hostId}` | Elimina tutte le residenze di un host |
| `DELETE` | `/residences` | Elimina tutte le residenze |

**Request Body Example (POST/PUT)**:
```json
{
  "name": "Villa Diamante",
  "address": "Costa Diamante 7",
  "numberOfRooms": 7,
  "guestCapacity": 5,
  "floor": 0,
  "price": 500.00,
  "availableFrom": "2025-06-01",
  "availableTo": "2025-09-01",
  "hostId": 1
}
```

---

### 📅 Bookings

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/bookings` | Crea nuova prenotazione |
| `GET` | `/bookings` | Lista tutte le prenotazioni |
| `GET` | `/bookings/{id}` | Dettaglio prenotazione per ID |
| `GET` | `/bookings/residence/{residenceId}` | Prenotazioni per residenza |
| `GET` | `/bookings/user/{userId}` | Prenotazioni per utente |
| `GET` | `/bookings/user/{userId}/last` | Ultima prenotazione utente |
| `PUT` | `/bookings/{id}` | Aggiorna prenotazione |
| `DELETE` | `/bookings/{id}` | Elimina prenotazione per ID |
| `DELETE` | `/bookings` | Elimina tutte le prenotazioni |

**Request Body Example (POST/PUT)**:
```json
{
  "startDate": "2025-07-10T15:30:00",
  "endDate": "2025-07-15T11:00:00",
  "residenceId": 1,
  "userId": 1
}
```

---

### ⭐ Feedbacks

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/feedbacks` | Crea nuovo feedback |
| `GET` | `/feedbacks` | Lista tutti i feedback |
| `GET` | `/feedbacks/{id}` | Dettaglio feedback per ID |
| `GET` | `/feedbacks/user/{userId}` | Feedback per utente |
| `GET` | `/feedbacks/booking/{bookingId}` | Feedback per prenotazione |
| `GET` | `/feedbacks/user/{userId}/booking/{bookingId}` | Feedback specifico |
| `PUT` | `/feedbacks/{id}` | Aggiorna feedback |
| `DELETE` | `/feedbacks/{id}` | Elimina feedback per ID |
| `DELETE` | `/feedbacks` | Elimina tutti i feedback |

**Request Body Example (POST/PUT)**:
```json
{
  "title": "Soggiorno Magnifico",
  "comment": "Posto incantevole, tutto perfetto.",
  "rating": 5,
  "bookingId": 1,
  "userId": 1
}
```

---

### 📈 Statistics

| Endpoint | Description | Return Type |
|----------|-------------|-------------|
| `/users/stats/mdb` | Utente con più giorni prenotati | `UserResponseDTO` |
| `/stats/hosts` | Top host dell'ultimo mese | `List<HostResponseDTO>` |
| `/residences/stats/mprlm` | Residenza più popolare | `ResidenceResponseDTO` |
| `/residences/stats/avg` | Media posti letto | `Double` |

## 📦 Dipendenze Maven

```xml
<dependencies>
    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.1</version>
    </dependency>
    
    <!-- HTTP Server (Javalin) -->
    <dependency>
        <groupId>io.javalin</groupId>
        <artifactId>javalin</artifactId>
        <version>5.6.3</version>
    </dependency>
    
    <!-- JSON Processing -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.16.1</version>
    </dependency>
    
    <!-- Logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.9</version>
    </dependency>
    
    <!-- Connection Pool (HikariCP) -->
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
        <version>5.1.0</version>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 🗺️ Relazioni tra Entità

```
Users (1) ────────────< (N) Bookings
                              │
Users (1) ────────────< (N) Feedbacks
                              │
Hosts (1) ────────────< (N) Residences
                              │
Residences (1) ───────< (N) Bookings
                              │
Bookings (1) ─────────< (1) Feedbacks

Users (1) ────────────= (1) Hosts (Inheritance)
```

## 🛠️ Pattern di Implementazione

### DAO Pattern Example

```java
// UserDAO.java
public class UserDAO {
    private final Connection connection;
    
    public User save(User user) throws SQLException {
        String sql = "INSERT INTO Users (first_name, last_name, email, password, address) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getAddress());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user.setId(rs.getLong("id"));
            }
            return user;
        }
    }
}
```

### Service Pattern Example

```java
// UserService.java
public class UserService {
    private final UserDAO userDAO;
    
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        // Validazione
        validateUserRequest(requestDTO);
        
        // Conversione DTO → Model
        User user = convertToModel(requestDTO);
        
        // Salvataggio
        User savedUser = userDAO.save(user);
        
        // Conversione Model → Response DTO
        return convertToResponseDTO(savedUser);
    }
}
```

### Controller Pattern Example

```java
// UserController.java (con Javalin)
public class UserController {
    private final UserService userService;
    
    public void createUser(Context ctx) {
        UserRequestDTO requestDTO = ctx.bodyAsClass(UserRequestDTO.class);
        UserResponseDTO response = userService.createUser(requestDTO);
        ctx.status(201).json(response);
    }
}
```

## 🚀 Deploy

### Build JAR

```bash
mvn clean package
# JAR generato in target/luxury-tourist-1.0.0.jar
```

### Esecuzione

```bash
java -jar target/luxury-tourist-1.0.0.jar
```

## 🛠️ Troubleshooting

**Errore connessione database**
```bash
sudo systemctl status postgresql
psql -U tourist_user -d luxury_tourist
```

**Porta 8080 già in uso**
```properties
# Cambia porta in application.properties
server.port=8081
```

## 📝 Postman Collection

Importa la collection Postman per testare tutti gli endpoint!

---

**Versione**: 1.0  
**Java**: 17+  
**Maven**: 3.8+  
**PostgreSQL**: 14+