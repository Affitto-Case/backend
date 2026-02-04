package com.giuseppe_tesse.turista.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMostDayBooking{
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Integer totalDays;
}