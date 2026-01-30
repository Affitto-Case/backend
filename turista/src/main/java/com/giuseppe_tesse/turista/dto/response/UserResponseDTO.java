package com.giuseppe_tesse.turista.dto.response;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    //USER attributes
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String address;
    private LocalDate registrationDate;
    
}
