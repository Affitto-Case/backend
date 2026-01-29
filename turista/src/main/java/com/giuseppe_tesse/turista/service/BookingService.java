package com.giuseppe_tesse.turista.service;

import java.time.LocalDateTime;
import java.util.List;

import com.giuseppe_tesse.turista.dao.BookingDAO;
import com.giuseppe_tesse.turista.exception.DuplicateBookingException;
import com.giuseppe_tesse.turista.exception.BookingNotFoundException;
import com.giuseppe_tesse.turista.exception.ResidenceNotFoundException;
import com.giuseppe_tesse.turista.model.Booking;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingService {

    private final BookingDAO bookingDAO;

    public BookingService(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    // ==================== CREATE ====================

    public Booking createBooking(Long residenceId,
                                 Long userId,
                                 LocalDateTime startDate,
                                 LocalDateTime endDate) {

        log.info(
            "Attempt to create booking - Residence ID: {}, User ID: {}, Start: {}, End: {}",
            residenceId, userId, startDate, endDate
        );

        List<Booking> existingBookings =
                bookingDAO.findByResidenceId(residenceId)
                        .orElseThrow(() ->
                                new ResidenceNotFoundException(residenceId)
                        );

        for (Booking booking : existingBookings) {
            if (startDate.isBefore(booking.getStartDate())
                    && endDate.isAfter(booking.getEndDate())) {

                throw new DuplicateBookingException(
                        "A booking already exists for the selected period"
                );
            }
        }

        Booking booking = new Booking(residenceId, userId, startDate, endDate);
        return bookingDAO.create(booking);
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
