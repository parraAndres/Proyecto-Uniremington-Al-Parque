package com.vetsync.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min=6)
    private String newPassword;
    @NotBlank
    private String token;
}
