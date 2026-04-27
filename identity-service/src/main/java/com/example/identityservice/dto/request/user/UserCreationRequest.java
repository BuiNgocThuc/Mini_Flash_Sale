package com.example.identityservice.dto.request.user;

import com.example.identityservice.validation.PasswordConstraint;
import com.example.identityservice.validation.SmartEmailConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 4, message = "USERNAME_INVALID")
    @NotBlank(message = "FIELD_REQUIRED")
    String username;

    @NotBlank(message = "FIELD_REQUIRED")
    @PasswordConstraint
    String password;

    @NotBlank(message = "FIELD_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    @SmartEmailConstraint(message = "EMAIL_INVALID")
    String email;

    @NotNull(message = "FIELD_REQUIRED")
    @Past(message = "DOB_INVALID")
    LocalDate dob;
}
