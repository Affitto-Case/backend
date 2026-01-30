package com.giuseppe_tesse.turista.service;

import java.time.LocalDateTime;
import java.util.List;

import com.giuseppe_tesse.turista.dao.BookingDAO;
import com.giuseppe_tesse.turista.exception.DuplicateBookingException;
import com.giuseppe_tesse.turista.exception.BookingNotFoundException;
import com.giuseppe_tesse.turista.exception.ResidenceNotFoundException;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.model.Residence;
import com.giuseppe_tesse.turista.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingService {

    private final BookingDAO bookingDAO;

    public BookingService(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    // ==================== CREATE ====================

    public Booking createBooking(Booking booking) {
    log.info("Attempt to create booking - Residence: {}, User: {}, Start: {}, End: {}",
            booking.getResidence(), booking.getUser(), booking.getStartDate(), booking.getEndDate());

    // Recupero prenotazioni esistenti per validazione
    List<Booking> existingBookings = bookingDAO.findByResidenceId(booking.getResidence().getId())
            .orElseThrow(() -> new ResidenceNotFoundException(booking.getResidence().getId()));

    for (Booking existing : existingBookings) {
        if (booking.getStartDate().isBefore(existing.getEndDate()) 
            && booking.getEndDate().isAfter(existing.getStartDate())) {
            
            log.warn("Booking conflict detected for residence ID: {}", booking.getResidence().getId());
            throw new DuplicateBookingException("A booking already exists for the selected period");
        }
    }

    Booking newBooking = new Booking(
            booking.getResidence(), 
            booking.getUser(), 
            booking.getStartDate(), 
            booking.getEndDate()
    );
    
    return bookingDAO.create(newBooking);
}
    // ==================== READ ====================

    public List<Booking> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingDAO.findAll();
    }

    public Booking getBookingById(Long id) {
        log.info("Fetching booking with ID: {}", id);
        return bookingDAO.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    public List<Booking> getBookingsByResidenceId(Long residenceId) {
        log.info("Fetching bookings for residence ID: {}", residenceId);
        return bookingDAO.findByResidenceId(residenceId)
                .orElseThrow(() -> new BookingNotFoundException(residenceId));
    }

    // ==================== UPDATE ====================

    public Booking updateBooking(Booking booking) {
        log.info("Updating booking with ID: {}", booking.getId());
        return bookingDAO.update(booking)
                .orElseThrow(() -> new BookingNotFoundException(booking.getId()));
    }

    // ==================== DELETE ====================

    public boolean deleteBookingById(Long id) {
        log.info("Attempting to delete booking with ID: {}", id);

        boolean deleted = bookingDAO.deleteById(id);
        if (!deleted) {
            log.warn("Delete failed - Booking not found with ID: {}", id);
            throw new BookingNotFoundException(id);
        }
        return true;
    }

    public int deleteAllBookings() {
        log.info("Deleting all bookings");
        return bookingDAO.deleteAll();
    }
}
