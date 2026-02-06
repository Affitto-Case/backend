package com.giuseppe_tesse.turista.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.giuseppe_tesse.turista.dao.BookingDAO;
import com.giuseppe_tesse.turista.dao.ResidenceDAO;
import com.giuseppe_tesse.turista.dto.TopHostDTO;
import com.giuseppe_tesse.turista.exception.BadRequestException;
import com.giuseppe_tesse.turista.exception.DuplicateBookingException;
import com.giuseppe_tesse.turista.exception.BookingNotFoundException;
import com.giuseppe_tesse.turista.exception.ResidenceNotFoundException;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.model.Host;
import com.giuseppe_tesse.turista.model.Residence;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingService {

    private final BookingDAO bookingDAO;
    private final ResidenceDAO residenceDAO;
    private final HostService hostService;

    public BookingService(BookingDAO bookingDAO, ResidenceDAO residenceDAO, HostService hostService) {
        this.bookingDAO = bookingDAO;
        this.residenceDAO = residenceDAO;
        this.hostService = hostService;
    }

    // ==================== CREATE ====================

    public Booking createBooking(Booking booking) {
        log.info("Attempt to create booking - Residence: {}, User: {}, Start: {}, End: {}",
                booking.getResidence().getId(), booking.getUser().getId(), booking.getStartDate(),
                booking.getEndDate());
        validateBooking(booking);
        Booking savedBooking = bookingDAO.create(booking);
        try {
            Residence residence = residenceDAO.findById(savedBooking.getResidence().getId())
                    .orElseThrow(() -> new ResidenceNotFoundException(savedBooking.getResidence().getId()));

            String hostCode = residence.getHost().getHost_code();

            refreshSuperHostStatus(hostCode, hostService);

        } catch (Exception e) {
            log.error("Error refreshing SuperHost status, but booking was created: ", e);
        }

        return savedBooking;
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

    public List<Booking> getBookingsByUserId(Long userId) {
        log.info("Fetching bookings for residence ID: {}", userId);
        return bookingDAO.findBookingsByUserId(userId)
                .orElseThrow(() -> new BookingNotFoundException(userId));
    }

    public Booking getLastBookingByUserId(Long userId) {
        log.info("Fetching last booking for user ID: {}", userId);
        return bookingDAO.findLastBookingByUserId(userId)
                .orElseThrow(() -> new BookingNotFoundException(userId));
    }

    public List<TopHostDTO> getMostPopularHostsLastMonth() {
        List<TopHostDTO> hosts = bookingDAO.getMostPopularHostsLastMonth();

        return hosts;
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

    // ==================== UTILITY ====================
    private void refreshSuperHostStatus(String hostCode, HostService hostService) {
        
        int totalBookings = bookingDAO.countTotalBookingsByHostCode(hostCode);
        Host host = hostService.getByHostCode(hostCode);
        boolean shouldBeSuper = totalBookings >= 100;
        host.setTotal_bookings(totalBookings);
        host.setSuperHost(shouldBeSuper);
        hostService.updateHostStats(host);
        log.info("Host {} stats updated. Total bookings: {}, SuperHost: {}", hostCode, totalBookings, shouldBeSuper);
    }

    private void validateBooking(Booking booking) {
        if (!booking.getEndDate().isAfter(booking.getStartDate())) {
            throw new BadRequestException("La data di fine deve essere successiva a quella di inizio");
        }
        LocalDateTime availableFrom = booking.getResidence()
                .getAvailable_from()
                .atStartOfDay();

        LocalDateTime availableTo = booking.getResidence()
                .getAvailable_to()
                .atTime(23, 59, 59);

        if (booking.getStartDate().isBefore(availableFrom) ||
                booking.getEndDate().isAfter(availableTo)) {
            throw new BadRequestException(
                    "Le date non rientrano nel periodo di disponibilità dell'abitazione");
        }

        if (booking.getResidence().getHost().getId().equals(booking.getUser().getId())) {
            throw new BadRequestException("Non puoi prenotare una tua abitazione");
        }

        if (booking.getStartDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Non puoi prenotare in giorni antecedenti a oggi");
        }

        List<Booking> existingBookings = bookingDAO.findByResidenceId(booking.getResidence().getId())
                .orElse(new ArrayList<>());

        for (Booking existing : existingBookings) {
            if (booking.getStartDate().isBefore(existing.getEndDate())
                    && booking.getEndDate().isAfter(existing.getStartDate())) {
                log.warn("Booking conflict detected for residence ID: {}", booking.getResidence().getId());
                throw new DuplicateBookingException("A booking already exists for the selected period");
            }
        }
    }

}