package com.giuseppe_tesse.turista.dto.response;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {

    //BOOKING attributes
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // RESIDENCE atribbutes
    private Long residenceId;
    private String residenceName;
    private String residenceAddress;
    private double pricePerNight;

    //USER attributes
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;

    // HOST attributes
    private Long hostId;
    private String hostName;  // firstName + lastName
    private String hostCode;
    
    // Campi calcolati
    private Integer numberOfNights;  // Calcolato da startDate e endDate
    private Double totalPrice;       // numberOfNights * pricePerNight
    private String status;           // "ACTIVE", "COMPLETED", "CANCELLED"

}
