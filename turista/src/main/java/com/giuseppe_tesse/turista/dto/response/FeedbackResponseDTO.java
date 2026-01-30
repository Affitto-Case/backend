package com.giuseppe_tesse.turista.dto.response;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponseDTO {

    // FEEDBACK attributes
    private Long id;
    private String title;
    private Integer rating;
    private String comment;

    //BOOKING attributes
    private Long bookingId;
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;

    // RESIDENCE atribbutes
    private Long residenceId;
    private String residenceName;
    private String residenceAddress;

    //USER attribute
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;

}
