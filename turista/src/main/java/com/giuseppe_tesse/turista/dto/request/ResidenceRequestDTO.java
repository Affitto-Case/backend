package com.giuseppe_tesse.turista.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResidenceRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "100000.00", message = "Price must be realistic")
    private Double price;  // Double invece di double per @NotNull

    @NotNull(message = "Number of rooms is required")
    @Min(value = 1, message = "At least 1 room is required")
    @Max(value = 50, message = "Number of rooms must be realistic")
    private Integer numberOfRooms;  // Integer invece di int

    @NotNull(message = "Guest capacity is required")
    @Min(value = 1, message = "At least 1 guest is required")
    @Max(value = 100, message = "Guest capacity must be realistic")
    private Integer guestCapacity;  // Integer invece di int

    @Min(value = -5, message = "Floor must be valid")
    @Max(value = 200, message = "Floor must be realistic")
    private Integer floor;  // Integer invece di int (può essere null)

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate availableFrom;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate availableTo;

    @NotNull(message = "Host ID is required")
    @Positive(message = "Host ID must be positive")
    private Long hostId;

    // Validazione personalizzata: availableTo deve essere dopo availableFrom
    @AssertTrue(message = "End date must be after start date")
    private boolean isDateRangeValid() {
        if (availableFrom == null || availableTo == null) {
            return true;
        }
        return availableTo.isAfter(availableFrom);
    }
}