package com.giuseppe_tesse.turista.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MostPopularResidenceDTO {
    private Long residenceId;
    private String name;
    private int totalBookings;
}

