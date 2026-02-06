package com.giuseppe_tesse.turista.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.giuseppe_tesse.turista.dao.BookingDAO;
import com.giuseppe_tesse.turista.dto.TopHostDTO;
import com.giuseppe_tesse.turista.exception.DuplicateBookingException;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.model.Host;
import com.giuseppe_tesse.turista.model.Residence;
import com.giuseppe_tesse.turista.model.User;
import com.giuseppe_tesse.turista.util.DatabaseConnection;
import com.giuseppe_tesse.turista.util.DateConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingDAOImpl implements BookingDAO {

private final String SELECT_ALL_JOIN =
    "SELECT \n" +
    "    b.id AS booking_id,\n" +
    "    b.start_date,\n" +
    "    b.end_date,\n" +
    "    b.residence_id,\n" +
    "    b.user_id AS guest_id,\n" +

    "    h.user_id AS host_id,\n" +
    "    h.host_code,\n" +

    "    guest.first_name AS guest_first_name,\n" +
    "    guest.last_name  AS guest_last_name,\n" +
    "    guest.email      AS guest_email,\n" +

    "    host_user.first_name AS host_first_name,\n" +
    "    host_user.last_name  AS host_last_name,\n" +
    "    host_user.email      AS host_email,\n" +

    "    r.name    AS residence_name,\n" +
    "    r.address AS residence_address,\n" +
    "    r.price\n" +

    "FROM bookings b\n" +
    "JOIN users guest        ON b.user_id = guest.id\n" +
    "JOIN residences r       ON b.residence_id = r.id\n" +
    "JOIN hosts h            ON r.host_id = h.user_id\n" +
    "JOIN users host_user    ON h.user_id = host_user.id";


private final String BOOKINGS_TOTAL_BY_HOST=
                  "SELECT h.user_id, COUNT(b.id) AS total_bookings\n" +
                  "FROM hosts h\n" +
                  "JOIN residences r ON h.user_id = r.host_id\n" + 
                  "JOIN bookings b ON r.id = b.residence_id\n" + 
                  "WHERE h.host_code = ?\n" + 
                  "GROUP BY h.user_id;\n";

// ==================== CREATE ====================

    @Override
    public Booking create(Booking booking) {
        log.info("Creating new booking for user ID: {} and residence ID: {}", booking.getUser().getId(), booking.getResidence().getId());
        String sql = "INSERT INTO bookings (start_date, end_date, residence_id, user_id) VALUES (?, ?, ?, ?) RETURNING id";
        
        if (existsOverlappingBooking(booking.getResidence().getId(), booking.getStartDate(), booking.getEndDate())) {
            log.warn("Booking creation failed: overlapping booking exists for residence ID: {} between {} and {}", 
                     booking.getResidence().getId(), booking.getStartDate(), booking.getEndDate());
            throw new DuplicateBookingException(
                "This residence is already booked for the selected dates"
            );
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, DateConverter.convertTimestampFromLocalDateTime(booking.getStartDate()));
            ps.setTimestamp(2, DateConverter.convertTimestampFromLocalDateTime(booking.getEndDate()));
            ps.setLong(3, booking.getResidence().getId());
            ps.setLong(4, booking.getUser().getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    booking.setId(rs.getLong("id"));
                    log.info("Booking created successfully with ID: {}", booking.getId());
                } else {
                    log.error("Creating booking failed, no ID obtained");
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            }

            return booking;

        } catch (SQLException e) {
            log.error("Error creating booking for user ID: {} and residence ID: {}", booking.getUser().getId(), booking.getResidence().getId(), e);
            throw new RuntimeException("Error creating booking", e);
        }
    }

// ==================== READ ====================

    @Override
    public Optional<Booking> findById(Long id) {
        log.info("Finding booking by ID: {}", id);
        String sql = SELECT_ALL_JOIN + " WHERE b.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    log.info("Successfully found booking with ID: {}", id);
                    return Optional.of(booking);
                }
            }
            
            log.warn("No booking found with ID: {}", id);
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error finding booking by ID: {}", id, e);
            throw new RuntimeException("Error finding booking by ID", e);
        }
    }

    @Override
    public List<Booking> findAll() {
        log.info("Retrieving all bookings");
        String sql = SELECT_ALL_JOIN;
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            log.info("Successfully retrieved {} bookings", bookings.size());
            return bookings;

        } catch (SQLException e) {
            log.error("Error retrieving all bookings", e);
            throw new RuntimeException("Error retrieving bookings", e);
        }
    }

    @Override
    public Optional<List<Booking>> findByResidenceId(Long residenceId) {
        log.info("Finding bookings for residence ID: {}", residenceId);
        String sql = SELECT_ALL_JOIN + " WHERE b.residence_id = ?";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, residenceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
            
            if (bookings.isEmpty()) {
                log.info("No bookings found for residence ID: {}", residenceId);
                return Optional.empty();
            }
            
            log.info("Successfully retrieved {} bookings for residence ID: {}", bookings.size(), residenceId);
            return Optional.of(bookings);

        } catch (SQLException e) {
            log.error("Error finding bookings by residence ID: {}", residenceId, e);
            throw new RuntimeException("Error finding bookings by residence ID", e);
        }
    }

    @Override
    public Optional<Booking> findLastBookingByUserId(Long user_id){
        log.info("Finding last booking for user ID: {}", user_id);
        String sql = SELECT_ALL_JOIN + " WHERE guest.id = ? ORDER BY b.start_date DESC LIMIT 1" ;
         try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, user_id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    log.info("Successfully found booking with user ID: {}", user_id);
                    return Optional.of(booking);
                }
            }
            
            log.warn("No booking found with user ID: {}", user_id);
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error finding booking by user ID: {}", user_id, e);
            throw new RuntimeException("Error finding booking by ID", e);
        }
    }

    @Override
    public Optional<List<Booking>> findBookingsByUserId(Long user_id){
        log.info("Finding last booking for user ID: {}", user_id);
        String sql = SELECT_ALL_JOIN + " WHERE guest.id = ? ORDER BY b.start_date DESC" ;
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, user_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
            if (bookings.isEmpty()) {
                log.info("No bookings found for user ID: {}", user_id);
                return Optional.empty();
            }
            
            log.info("Successfully retrieved {} bookings for user ID: {}", bookings.size(), user_id);
            return Optional.of(bookings);

        } catch (SQLException e) {
            log.error("Error finding booking by user ID: {}", user_id, e);
            throw new RuntimeException("Error finding booking by ID", e);
        }
    }

    @Override
    public List<TopHostDTO> getMostPopularHostsLastMonth() {
    log.info("Fetching top 5 hosts by bookings last month");

    String sql = """
        SELECT 
            h.user_id AS host_id,
            h.host_code,
            u.first_name,
            u.last_name,
            u.email,
            COUNT(b.id) AS total_bookings
        FROM hosts h
        JOIN users u      ON h.user_id = u.id
        JOIN residences r ON r.host_id = h.user_id
        JOIN bookings b   ON r.id = b.residence_id
        WHERE b.start_date >= date_trunc('month', current_date - interval '1 month')
          AND b.start_date <  date_trunc('month', current_date)
        GROUP BY h.user_id, h.host_code, u.first_name, u.last_name, u.email
        ORDER BY total_bookings DESC
        LIMIT 5
    """;

    List<TopHostDTO> result = new ArrayList<>();

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            TopHostDTO dto = new TopHostDTO();
            dto.setHostId(rs.getLong("host_id"));
            dto.setHostCode(rs.getString("host_code"));
            dto.setFirstName(rs.getString("first_name"));
            dto.setLastName(rs.getString("last_name"));
            dto.setEmail(rs.getString("email"));
            dto.setTotalBookings(rs.getInt("total_bookings"));

            result.add(dto);
        }

        return result;

    } catch (SQLException e) {
        log.error("Error fetching top hosts", e);
        throw new RuntimeException("Error fetching top hosts", e);
    }
}

    public Integer getBookingCount() {
        
        log.info("Return count of all booking");
        String sql = "SELECT COUNT(b.id) AS total FROM bookings b";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                Integer count = rs.getInt("total");

                log.info("Successfully retrieved {} bookings", count);
                return count;
            }

            return 0;

        } catch (SQLException e) {
            log.error("Error retrieving all bookings", e);
            throw new RuntimeException("Error retrieving bookings", e);
        }
    }



    
// ==================== UPDATE ====================

    @Override
    public Optional<Booking> update(Booking booking) {
        log.info("Updating booking with ID: {}", booking.getId());
        String sql = "UPDATE bookings " +
                    "SET start_date = ?, end_date = ?, residence_id = ?, user_id = ? " +
                    "WHERE id = ? " +
                    "RETURNING id, start_date, end_date, residence_id, user_id";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, DateConverter.convertTimestampFromLocalDateTime(booking.getStartDate()));
            ps.setTimestamp(2, DateConverter.convertTimestampFromLocalDateTime(booking.getEndDate()));
            ps.setLong(3, booking.getResidence().getId());
            ps.setLong(4, booking.getUser().getId());
            ps.setLong(5, booking.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking updatedBooking = getBookingById(rs.getLong("id"), conn);
                    log.info("Successfully updated booking with ID: {}", booking.getId());
                    return Optional.of(updatedBooking);
                }
            }

            log.warn("No booking updated with ID: {}", booking.getId());
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Error updating booking with ID: {}", booking.getId(), e);
            throw new RuntimeException("Error updating booking", e);
        }
    }

// ==================== DELETE ====================

    @Override
    public boolean deleteById(Long id) {
        log.info("Deleting booking with ID: {}", id);
        String sql = "DELETE FROM bookings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                log.info("Successfully deleted booking with ID: {}", id);
                return true;
            }
            
            log.warn("No booking found to delete with ID: {}", id);
            return false;

        } catch (SQLException e) {
            log.error("Error deleting booking with ID: {}", id, e);
            throw new RuntimeException("Error deleting booking", e);
        }
    }

    @Override
    public int deleteAll() {
        log.info("Deleting all bookings");
        String sql = "DELETE FROM bookings";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int deleted = ps.executeUpdate();
            log.info("Successfully deleted {} bookings", deleted);
            return deleted;

        } catch (SQLException e) {
            log.error("Error deleting all bookings", e);
            throw new RuntimeException("Error deleting all bookings", e);
        }
    }

// ==================== UTILITY ====================

   private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
    Booking booking = new Booking();

    booking.setId(rs.getLong("booking_id"));
    booking.setStartDate(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("start_date")));
    booking.setEndDate(DateConverter.convertLocalDateTimeFromTimestamp(rs.getTimestamp("end_date")));

    // ----- Guest (user che prenota) -----
    User guest = new User();
    guest.setId(rs.getLong("guest_id"));
    guest.setFirstName(rs.getString("guest_first_name"));
    guest.setLastName(rs.getString("guest_last_name"));
    guest.setEmail(rs.getString("guest_email"));
    booking.setUser(guest);

    // ----- Host -----
    Host host = new Host();
    host.setId(rs.getLong("host_id"));
    host.setHost_code(rs.getString("host_code"));
    host.setFirstName(rs.getString("host_first_name"));
    host.setLastName(rs.getString("host_last_name"));
    host.setEmail(rs.getString("host_email"));

    // ----- Residence -----
    Residence residence = new Residence();
    residence.setId(rs.getLong("residence_id"));
    residence.setName(rs.getString("residence_name"));
    residence.setAddress(rs.getString("residence_address"));
    residence.setPrice_per_night(rs.getDouble("price"));
    residence.setHost(host);

    booking.setResidence(residence);

    return booking;
}


    public boolean existsOverlappingBooking(Long residenceId, LocalDateTime start, LocalDateTime end) {
        log.debug("Checking for overlapping bookings for residence ID: {} between {} and {}", residenceId, start, end);
        String sql = """
            SELECT 1
            FROM bookings
            WHERE residence_id = ?
            AND start_date < ?
            AND end_date > ?
            LIMIT 1
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, residenceId);
            ps.setTimestamp(2, DateConverter.convertTimestampFromLocalDateTime(end));
            ps.setTimestamp(3, DateConverter.convertTimestampFromLocalDateTime(start));

            try (ResultSet rs = ps.executeQuery()) {
                boolean exists = rs.next();
                if (exists) {
                    log.debug("Overlapping booking found for residence ID: {}", residenceId);
                } else {
                    log.debug("No overlapping bookings found for residence ID: {}", residenceId);
                }
                return exists;
            }

        } catch (SQLException e) {
            log.error("Error checking overlapping bookings for residence ID: {}", residenceId, e);
            throw new RuntimeException("Error checking overlapping bookings", e);
        }
    }

@Override
public int countTotalBookingsByHostCode(String hostCode) {
    log.info("Counting total bookings for host code: {}", hostCode);
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(BOOKINGS_TOTAL_BY_HOST)) {

        ps.setString(1, hostCode);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_bookings"); 
            }
        }
        return 0; 

    } catch (SQLException e) {
        log.error("Error counting bookings for host: {}", hostCode, e);
        throw new RuntimeException("Error executing booking count", e);
    }
}

private Booking getBookingById(long bookingId, Connection conn) throws SQLException {
    String sql = SELECT_ALL_JOIN +"\nWHERE b.id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setLong(1, bookingId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToBooking(rs);
            } else {
                throw new SQLException("Booking not found with ID " + bookingId);
            }
        }
    }
}

}