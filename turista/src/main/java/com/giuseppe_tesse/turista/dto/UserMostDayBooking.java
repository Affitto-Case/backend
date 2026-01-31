package com.giuseppe_tesse.turista.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMostDayBooking{
    private Long userId;
    private Integer totalDays;
}