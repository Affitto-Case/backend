package com.giuseppe_tesse.turista.dto.response;

import java.time.LocalDate;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResidenceResponseDTO {

    //RESIDENCE attributes
    private Long id;
    private String name;
    private String address;
    private Double pricePerNight;
    private Integer numberOfRooms;
    private Integer guestCapacity;
    private Integer floor;
    private LocalDate availableFrom;
    private LocalDate availableTo;


    //HOST attributes
    private Long hostId;
    private String hostName; // firstName + lastName
    private String hostEmail;
    private String hostCode;

}
