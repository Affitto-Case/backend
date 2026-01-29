package com.giuseppe_tesse.turista.dto.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;


    @NotBlank(message = "Email is required")
    @Pattern(
        regexp = "^[^@]+@[^@]+\\.[^@]+$",
        message = "Email must be valid (format: user@domain.com)"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Password must contain uppercase, lowercase and number"
    )
    private String password;

    @NotBlank(message = "Address is required")
    private String address;

    
}
