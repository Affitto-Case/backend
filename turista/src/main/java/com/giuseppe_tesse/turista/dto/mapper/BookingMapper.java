package com.giuseppe_tesse.turista.dto.mapper;

import com.giuseppe_tesse.turista.dto.request.BookingRequestDTO;
import com.giuseppe_tesse.turista.dto.response.BookingResponseDTO;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.model.Residence;
import com.giuseppe_tesse.turista.model.User;

import java.time.temporal.ChronoUnit;

public class BookingMapper {

    // ==================== REQUEST DTO → ENTITY ====================
    
    /**
     * Converte BookingRequestDTO in Booking entity
     * 
     * @param dto BookingRequestDTO con i dati della richiesta
     * @param user User che effettua la prenotazione
     * @param residence Residence da prenotare
     * @return Booking entity (senza ID, sarà generato dal DB)
     */
    public static Booking toEntity(BookingRequestDTO dto, User user, Residence residence) {
        if (dto == null) {
            return null;
        }
        
        Booking booking = new Booking();
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setUser(user);
        booking.setResidence(residence);
        
        return booking;
    }

    // ==================== ENTITY → RESPONSE DTO ====================
    
    /**
     * Converte Booking entity in BookingResponseDTO
     * Include informazioni complete su Residence, User e Host
     * 
     * @param booking Booking entity da convertire
     * @return BookingResponseDTO con tutti i dati
     */
    public static BookingResponseDTO toResponseDTO(Booking booking) {
        if (booking == null) {
            return null;
        }
        
        BookingResponseDTO dto = new BookingResponseDTO();
        
        dto.setId(booking.getId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        
        if (booking.getResidence() != null) {
            Residence residence = booking.getResidence();
            dto.setResidenceId(residence.getId());
            dto.setResidenceName(residence.getName());
            dto.setResidenceAddress(residence.getAddress());
            dto.setPricePerNight(residence.getPrice_per_night());
            
            if (residence.getHost() != null) {
                dto.setHostId(residence.getHost().getId());
                dto.setHostName(
                    residence.getHost().getFirstName() + " " + 
                    residence.getHost().getLastName()
                );
            }
        }
        
        if (booking.getUser() != null) {
            User user = booking.getUser();
            dto.setUserId(user.getId());
            dto.setUserFirstName(user.getFirstName());
            dto.setUserLastName(user.getLastName());
            dto.setUserEmail(user.getEmail());
        }
        
        if (booking.getStartDate() != null && booking.getEndDate() != null) {
            // Calcola il numero di notti
            long nights = ChronoUnit.DAYS.between(
                booking.getStartDate().toLocalDate(), 
                booking.getEndDate().toLocalDate()
            );
            dto.setNumberOfNights((int) nights);
            
            // Calcola il prezzo totale
            if (booking.getResidence() != null && booking.getResidence().getPrice_per_night() > 0) {
                dto.setTotalPrice(nights * booking.getResidence().getPrice_per_night());
            }
        }
        
        return dto;
    }

    // ==================== UTILITY METHODS (opzionali) ====================
    
    /**
     * Aggiorna un'entità Booking esistente con i dati del DTO
     * Utile per le operazioni di UPDATE
     * 
     * @param existingBooking Booking entity esistente da aggiornare
     * @param dto BookingRequestDTO con i nuovi dati
     * @return Booking entity aggiornata
     */
    public static Booking updateEntity(Booking existingBooking, BookingRequestDTO dto) {
        if (existingBooking == null || dto == null) {
            return existingBooking;
        }
        
        // Aggiorna solo i campi modificabili
        if (dto.getStartDate() != null) {
            existingBooking.setStartDate(dto.getStartDate());
        }
        
        if (dto.getEndDate() != null) {
            existingBooking.setEndDate(dto.getEndDate());
        }
        
        
        return existingBooking;
    }
    
    /**
     * Versione semplificata del DTO Response (per liste)
     * Meno dettagli, più leggero
     * 
     * @param booking Booking entity
     * @return BookingResponseDTO minimale
     */
    public static BookingResponseDTO toResponseDTOSimple(Booking booking) {
        if (booking == null) {
            return null;
        }
        
        BookingResponseDTO dto = new BookingResponseDTO();
        
        dto.setId(booking.getId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        
        if (booking.getResidence() != null) {
            dto.setResidenceId(booking.getResidence().getId());
            dto.setResidenceName(booking.getResidence().getName());
        }
        
        if (booking.getUser() != null) {
            dto.setUserId(booking.getUser().getId());
            dto.setUserFirstName(booking.getUser().getFirstName());
            dto.setUserLastName(booking.getUser().getLastName());
        }
        
        return dto;
    }
    
    /**
     * Previene istanziazione - classe con solo metodi static
     */
    private BookingMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}