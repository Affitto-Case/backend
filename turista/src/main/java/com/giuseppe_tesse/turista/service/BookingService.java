package com.giuseppe_tesse.turista.service;

import java.util.ArrayList;
import java.util.List;

import com.giuseppe_tesse.turista.dao.BookingDAO;
import com.giuseppe_tesse.turista.dao.ResidenceDAO;
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
                booking.getResidence().getId(), booking.getUser().getId(), booking.getStartDate(), booking.getEndDate());
        List<Booking> existingBookings = bookingDAO.findByResidenceId(booking.getResidence().getId()).orElse(new ArrayList<>());

        for (Booking existing : existingBookings) {
            if (booking.getStartDate().isBefore(existing.getEndDate()) 
                && booking.getEndDate().isAfter(existing.getStartDate())) {
                log.warn("Booking conflict detected for residence ID: {}", booking.getResidence().getId());
                throw new DuplicateBookingException("A booking already exists for the selected period");
            }
        }
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

    public void refreshSuperHostStatus(String hostCode,HostService hostService) {
    // 1. Conta le prenotazioni totali
    int totalBookings = bookingDAO.countTotalBookingsByHostCode(hostCode);
    
    // 2. Recupera l'Host
    Host host = hostService.getByHostCode(hostCode);
    
    // 3. Logica della soglia
    boolean shouldBeSuper = totalBookings >= 100;
    
    // 4. Aggiorna solo se lo stato è cambiato (per ottimizzare il DB)
    if (host.isSuperHost() != shouldBeSuper) {
        host.setSuperHost(shouldBeSuper);
        hostService.updateHostStatus(host);
        
        log.info("Host {} status changed. SuperHost: {}", hostCode, shouldBeSuper);
    }
}
}
