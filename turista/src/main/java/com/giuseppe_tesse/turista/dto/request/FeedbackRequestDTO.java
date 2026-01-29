package com.giuseppe_tesse.turista.dto.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequestDTO {


    @NotNull(message = "Booking ID is required")
    @Positive(message = "Booking ID must be positive")
    private Long bookingId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 10 characters")
    private String title;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be greater than 0")
    @Max(value = 5, message = "Rating must be lower than 6")
    private Integer rating;

    @NotBlank(message = "Comment is required")
    @Size(max = 500, message = "Comment must not exceed 50 characters")
    private String comment;
    
}
