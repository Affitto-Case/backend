package com.giuseppe_tesse.turista.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostResponseDTO {

    // ID unico
    private Long id;

    // USER attributes
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private LocalDate registrationDate;

    // HOST attributes
    private String hostCode;
    private Integer totalBookings;
    private Boolean isSuperHost;  // Calcolato: totalBookings >= 100

}