package com.giuseppe_tesse.turista.dto.mapper;

import com.giuseppe_tesse.turista.dto.request.FeedbackRequestDTO;
import com.giuseppe_tesse.turista.dto.response.FeedbackResponseDTO;
import com.giuseppe_tesse.turista.model.Booking;
import com.giuseppe_tesse.turista.model.Feedback;
import com.giuseppe_tesse.turista.model.User;

public class FeedbackMapper {

    // ==================== REQUEST DTO → ENTITY ====================
    
    /**
     * Converte FeedbackRequestDTO in Feedback entity
     * 
     * @param dto FeedbackRequestDTO con i dati della richiesta
     * @param user User che lascia il feedback
     * @param booking Booking a cui si riferisce il feedback
     * @return Feedback entity (senza ID, sarà generato dal DB)
     */
    public static Feedback toEntity(FeedbackRequestDTO dto, User user, Booking booking) {
        if (dto == null) {
            return null;
        }
        
        Feedback feedback = new Feedback();
        feedback.setTitle(dto.getTitle());
        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());
        feedback.setUser(user);
        feedback.setBooking(booking);
        
        return feedback;
    }

    // ==================== ENTITY → RESPONSE DTO ====================
    
    /**
     * Converte Feedback entity in FeedbackResponseDTO
     * Include informazioni complete su Booking, Residence, User e Host
     * 
     * @param feedback Feedback entity da convertire
     * @return FeedbackResponseDTO con tutti i dati
     */
    public static FeedbackResponseDTO toResponseDTO(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        
        dto.setId(feedback.getId());
        dto.setTitle(feedback.getTitle());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        
        if (feedback.getBooking() != null) {
            Booking booking = feedback.getBooking();
            dto.setBookingId(booking.getId());
            dto.setBookingStartDate(booking.getStartDate());
            dto.setBookingEndDate(booking.getEndDate());
            
            if (booking.getResidence() != null) {
                dto.setResidenceId(booking.getResidence().getId());
                dto.setResidenceName(booking.getResidence().getName());
                dto.setResidenceAddress(booking.getResidence().getAddress());
            }
        }
        
        if (feedback.getUser() != null) {
            User user = feedback.getUser();
            dto.setUserId(user.getId());
            dto.setUserFirstName(user.getFirstName());
            dto.setUserLastName(user.getLastName());
            dto.setUserEmail(user.getEmail());
        }
        
        return dto;
    }

    // ==================== UTILITY METHODS ====================
    
    /**
     * Aggiorna un'entità Feedback esistente con i dati del DTO
     * 
     * @param existingFeedback Feedback entity esistente
     * @param dto FeedbackRequestDTO con i nuovi dati
     * @return Feedback entity aggiornata
     */
    public static Feedback updateEntity(Feedback existingFeedback, FeedbackRequestDTO dto) {
        if (existingFeedback == null || dto == null) {
            return existingFeedback;
        }
        
        if (dto.getTitle() != null) {
            existingFeedback.setTitle(dto.getTitle());
        }
        
        if (dto.getRating() != null) {
            existingFeedback.setRating(dto.getRating());
        }
        
        if (dto.getComment() != null) {
            existingFeedback.setComment(dto.getComment());
        }
        
        // NON aggiornare user e booking
        
        return existingFeedback;
    }
    
    /**
     * Versione semplificata per liste
     */
    public static FeedbackResponseDTO toResponseDTOSimple(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        
        dto.setId(feedback.getId());
        dto.setTitle(feedback.getTitle());
        dto.setRating(feedback.getRating());
        
        if (feedback.getUser() != null) {
            dto.setUserFirstName(feedback.getUser().getFirstName());
            dto.setUserLastName(feedback.getUser().getLastName());
        }
        
        if (feedback.getBooking() != null && feedback.getBooking().getResidence() != null) {
            dto.setResidenceName(feedback.getBooking().getResidence().getName());
        }
        
        return dto;
    }
    
    /**
     * Previene istanziazione
     */
    private FeedbackMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}