package com.example.identityservice.dto.request.user;

import com.example.identityservice.validation.PasswordConstraint;
import com.example.identityservice.validation.SmartEmailConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
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
    @Email(message = "EMAIL_INVALID") // Kiểm tra định dạng email nói chung
    @SmartEmailConstraint
    String email;
}
