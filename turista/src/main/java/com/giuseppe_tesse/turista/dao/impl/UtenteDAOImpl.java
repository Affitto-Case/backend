package com.giuseppe_tesse.turista.dao.impl;

import com.giuseppe_tesse.turista.model.Utente;
import com.giuseppe_tesse.turista.exception.UserNotFoundException;
import com.giuseppe_tesse.turista.exception.DuplicateUserException;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import com.giuseppe_tesse.turista.util.DateConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



import com.giuseppe_tesse.turista.dao.UtenteDAO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UtenteDAOImpl implements UtenteDAO {

    


// ==================== CREATE ====================

    @Override
    public Utente create(Utente utente) {
        String sql = "INSERT INTO utenti (nome,cognome,email, password, indirizzo, data_registrazione) VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, utente.getNome());
            ps.setString(2, utente.getCognome());
            ps.setString(3, utente.getEmail());
            ps.setString(4, utente.getPassword());
            ps.setString(5, utente.getIndirizzo());
            ps.setDate(6, DateConverter.toSqlDate(utente.getData_registrazione()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    utente.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            log.info("User created with ID: " + utente.getId());
            return utente;

        } catch (DuplicateUserException e) {
            log.error("Duplicate email: " + utente.getEmail(), e);
            throw new DuplicateUserException("Email already exists: " + utente.getEmail());
        } catch (SQLException e) {
            log.error("Error creating user", e);
            throw new RuntimeException("Error creating user", e);
        }
    }

// ==================== READ ====================

    @Override
    public List<Utente> findAll() {
        String sql = "SELECT id, nome, cognome, email, password, indirizzo, data_registrazione FROM utenti";
        List<Utente> utenti = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                utenti.add(mapResultSetToUtente(rs));
            }

            log.info("Retrieved " + utenti.size() + " users.");
            return utenti;

        }catch (SQLException e) {
            log.error("Error retrieving users", e);
            throw new RuntimeException("Error retrieving users", e);
        } 
    }

    @Override
    public Optional<Utente> findById(Long id) {
        String sql = "SELECT id, nome, cognome, email, password, indirizzo, data_registrazione FROM utenti WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUtente(rs));
                }
            } catch (UserNotFoundException e) {
                log.error("User not found with ID: " + id, e);
                throw new UserNotFoundException("User not found with ID: " + id);
            }

        } 
        catch (SQLException e) {
            log.error("Error finding user by ID: " + id, e);
            throw new RuntimeException("Error finding user by ID: " + id, e);
        }
        log.debug("No user found with ID: " + id);
        return Optional.empty();
}

    @Override
    public Optional<Utente> findByEmail(String email) {
        String sql = "SELECT id, nome, cognome, email, password, indirizzo, data_registrazione FROM utenti WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUtente(rs));
                }
            } catch (UserNotFoundException e) {
                log.error("User not found with email: " + email, e);
                throw new UserNotFoundException("User not found with email: " + email);
            }

        } 
        catch (SQLException e) {
            log.error("Error finding user by email: " + email, e);
            throw new RuntimeException("Error finding user by email: " + email, e);
        }
        log.debug("No user found with email: " + email);
        return Optional.empty();
    }


// ==================== UPDATE ====================

    @Override
    public Optional<Utente> update(Utente utente) {
        String sql = "UPDATE utenti SET nome = ?, cognome = ?, email = ?, password = ?, indirizzo = ?, data_registrazione = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, utente.getNome());
            ps.setString(2, utente.getCognome());
            ps.setString(3, utente.getEmail());
            ps.setString(4, utente.getPassword());
            ps.setString(5, utente.getIndirizzo());
            ps.setDate(6, DateConverter.toSqlDate(utente.getData_registrazione()));
            ps.setLong(7, utente.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                log.info("User updated with ID: " + utente.getId());
                return Optional.of(utente);
            } else {
                log.debug("No user found to update with ID: " + utente.getId());
                return Optional.empty();
            }

        } catch (SQLException e) {
            log.error("Error updating user with ID: " + utente.getId(), e);
            throw new RuntimeException("Error updating user with ID: " + utente.getId(), e);
        }
    }


// ==================== DELETE ====================


    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM utenti WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("User deleted with ID: " + id);
                return true;
            } else {
                log.debug("No user found to delete with ID: " + id);
                return false;
            }

        } catch (SQLException e) {
            log.error("Error deleting user with ID: " + id, e);
            throw new RuntimeException("Error deleting user with ID: " + id, e);
        }
    }

    @Override
    public int deleteAll() {
        String sql = "DELETE FROM utenti";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int rowsAffected = ps.executeUpdate();
            log.info("Deleted " + rowsAffected + " users.");
            return rowsAffected;

        } catch (SQLException e) {
            log.error("Error deleting all users", e);
            throw new RuntimeException("Error deleting all users", e);
        }
    }

    @Override
    public boolean deleteByEmail(String email) {
        String sql = "DELETE FROM utenti WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("User deleted with email: " + email);
                return true;
            } else {
                log.debug("No user found to delete with email: " + email);
                return false;
            }

        } catch (SQLException e) {
            log.error("Error deleting user with email: " + email, e);
            throw new RuntimeException("Error deleting user with email: " + email, e);
        }
    }



// ==================== UTILITY ====================

    private Utente mapResultSetToUtente(ResultSet rs) throws SQLException {
        Utente utente = new Utente();
        utente.setId(rs.getLong("id"));
        utente.setNome(rs.getString("nome"));
        utente.setCognome(rs.getString("cognome"));
        utente.setEmail(rs.getString("email"));
        utente.setPassword(rs.getString("password"));
        utente.setIndirizzo(rs.getString("indirizzo"));
        utente.setData_registrazione(DateConverter.date2LocalDate(rs.getDate("data_registrazione")));
        return utente;
    }

}




// package com.davidefella.repository;

// import java.sql.Connection;
// import java.sql.Date;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import com.davidefella.model.UserDemoJDBC;
// import com.davidefella.util.DatabaseConnection;
// import com.davidefella.util.DateConverter;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// public class UserDemoJDBCDAOImpl implements UserDemoJDBCDAO {

//     // ==================== CREATE ====================

//     @Override
//     public UserDemoJDBC create(UserDemoJDBC user) {
//         String sql = "INSERT INTO public.user_demo_jdbc(username, email, user_password, birthdate) VALUES(?, ?, ?, ?) RETURNING id, created_at";

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(sql)) {

//             ps.setString(1, user.getUsername());
//             ps.setString(2, user.getEmail());
//             ps.setString(3, user.getUserPassword());
//             ps.setDate(4, Date.valueOf(user.getBirthdate()));

//             try (ResultSet rs = ps.executeQuery()) {
//                 if (rs.next()) {
//                     user.setId(rs.getInt("id"));
//                     user.setCreatedAt(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("created_at")));
//                 }
//             }

//         } catch (SQLException ex) {
//             log.error("Errore durante la creazione dell'utente: {}", user.getEmail(), ex);
//             throw new RuntimeException("SQLException: ", ex);
//         }

//         log.info("Utente creato con successo - ID: {}, Email: {}", user.getId(), user.getEmail());
//         return user;
//     }

//     // ==================== READ ====================

//     @Override
//     public List<UserDemoJDBC> findAll() {
//         String sql = "SELECT id, username, email, user_password, birthdate, created_at FROM public.user_demo_jdbc";
//         List<UserDemoJDBC> users = new ArrayList<>();

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(sql);
//              ResultSet rs = ps.executeQuery()) {

//             while (rs.next()) {
//                 users.add(mapResultSetToUser(rs));
//             }

//         } catch (SQLException ex) {
//             log.error("Errore durante il recupero di tutti gli utenti", ex);
//             throw new RuntimeException("SQLException: ", ex);
//         }

//         log.info("Recuperati {} utenti", users.size());
//         return users;
//     }

//     @Override
//     public Optional<UserDemoJDBC> findById(Integer id) {
//         String sql = "SELECT id, username, email, user_password, birthdate, created_at FROM public.user_demo_jdbc WHERE id = ?";

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(sql)) {

//             ps.setInt(1, id);

//             try (ResultSet rs = ps.executeQuery()) {
//                 if (rs.next()) {
//                     return Optional.of(mapResultSetToUser(rs));
//                 }
//             }

//         } catch (SQLException ex) {
//             log.error("Errore durante la ricerca per ID: {}", id, ex);
//             throw new RuntimeException("SQLException: ", ex);
//         }

//         log.debug("Nessun utente trovato con ID: {}", id);
//         return Optional.empty();
//     }

//     @Override
//     public Optional<UserDemoJDBC> findByEmail(String email) {
//         String sql = "SELECT id, username, email, user_password, birthdate, created_at FROM public.user_demo_jdbc WHERE email = ?";

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(sql)) {

//             ps.setString(1, email);

//             try (ResultSet rs = ps.executeQuery()) {
//                 if (rs.next()) {
//                     return Optional.of(mapResultSetToUser(rs));
//                 }
//             }

//         } catch (SQLException ex) {
//             log.error("Errore durante la ricerca per email: {}", email, ex);
//             throw new RuntimeException("SQLException: ", ex);
//         }

//         log.debug("Nessun utente trovato con email: {}", email);
//         return Optional.empty();
//     }

//     @Override
//     public Optional<UserDemoJDBC> findByUsername(String username) {
//         String sql = "SELECT id, username, email, user_password, birthdate, created_at FROM public.user_demo_jdbc WHERE username = ?";

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(sql)) {

//             ps.setString(1, username);

//             try (ResultSet rs = ps.executeQuery()) {
//                 if (rs.next()) {
//                     return Optional.of(mapResultSetToUser(rs));
//                 }
//             }

//         } catch (SQLException ex) {
//             log.error("Errore durante la ricerca per username: {}", username, ex);
//             throw new RuntimeException("SQLException: ", ex);
//         }

//         log.debug("Nessun utente trovato con username: {}", username);
//         return Optional.empty();
//     }

//     // ==================== UPDATE ====================

//     @Override
//     public Optional<UserDemoJDBC> update(UserDemoJDBC user) {
//         String sql = "UPDATE public.user_demo_jdbc SET username = ?, email = ?, user_password = ?, birthdate = ? WHERE id = ? RETURNING id, username, email, user_password, birthdate, created_at";

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(sql)) {

//             ps.setString(1, user.getUsername());
//             ps.setString(2, user.getEmail());
//             ps.setString(3, user.getUserPassword());
//             ps.setDate(4, Date.valueOf(user.getBirthdate()));
//             ps.setInt(5, user.getId());

//             try (ResultSet rs = ps.executeQuery()) {
//                 if (rs.next()) {
//                     return Optional.of(mapResultSetToUser(rs));
//                 }
//             }

//         } catch (SQLException ex) {
//             log.error("Errore durante l'aggiornamento dell'utente ID: {}", user.getId(), ex);
//             throw new RuntimeException("SQLException: ", ex);
//         }

//         log.debug("Nessun utente aggiornato con ID: {}", user.getId());
//         return Optional.empty();
//     }

//     // ==================== DELETE ====================

//     @Override
//     public int deleteAll() {
//         String sql = "DELETE FROM public.user_demo_jdbc";

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(sql)) {

//             int deleted = ps.executeUpdate();
//             log.info("Eliminati {} utenti", deleted);
//             return deleted;

//         } catch (SQLException ex) {
//             log.error("Errore durante l'eliminazione di tutti gli utenti", ex);
//             throw new RuntimeException("SQLException: ", ex);
//         }
//     }

//     @Override
//     public boolean deleteById(Integer id) {
//         String sql = "DELETE FROM public.user_demo_jdbc WHERE id = ?";

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(sql)) {

//             ps.setInt(1, id);
//             int rowsAffected = ps.executeUpdate();

//             if (rowsAffected > 0) {
//                 log.info("Utente eliminato con ID: {}", id);
//                 return true;
//             }
//             log.debug("Nessun utente eliminato con ID: {}", id);
//             return false;

//         } catch (SQLException ex) {
//             log.error("Errore durante l'eliminazione per ID: {}", id, ex);
//             throw new RuntimeException("SQLException: ", ex);
//         }
//     }

//     @Override
//     public boolean deleteByEmail(String email) {
//         String sql = "DELETE FROM public.user_demo_jdbc WHERE email = ?";

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(sql)) {

//             ps.setString(1, email);
//             int rowsAffected = ps.executeUpdate();

//             if (rowsAffected > 0) {
//                 log.info("Utente eliminato con email: {}", email);
//                 return true;
//             }
//             log.debug("Nessun utente eliminato con email: {}", email);
//             return false;

//         } catch (SQLException ex) {
//             log.error("Errore durante l'eliminazione per email: {}", email, ex);
//             throw new RuntimeException("SQLException: ", ex);
//         }
//     }

//     @Override
//     public boolean deleteByUsername(String username) {
//         String sql = "DELETE FROM public.user_demo_jdbc WHERE username = ?";

//         try (Connection conn = DatabaseConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(sql)) {

//             ps.setString(1, username);
//             int rowsAffected = ps.executeUpdate();

//             if (rowsAffected > 0) {
//                 log.info("Utente eliminato con username: {}", username);
//                 return true;
//             }
//             log.debug("Nessun utente eliminato con username: {}", username);
//             return false;

//         } catch (SQLException ex) {
//             log.error("Errore durante l'eliminazione per username: {}", username, ex);
//             throw new RuntimeException("SQLException: ", ex);
//         }
//     }

//     // ==================== UTILITY ====================

//     private UserDemoJDBC mapResultSetToUser(ResultSet rs) throws SQLException {
//         UserDemoJDBC user = new UserDemoJDBC();
//         user.setId(rs.getInt("id"));
//         user.setUsername(rs.getString("username"));
//         user.setEmail(rs.getString("email"));
//         user.setUserPassword(rs.getString("user_password"));
//         user.setBirthdate(DateConverter.date2LocalDate(rs.getDate("birthdate")));
//         user.setCreatedAt(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("created_at")));
//         return user;
//     }

// }
