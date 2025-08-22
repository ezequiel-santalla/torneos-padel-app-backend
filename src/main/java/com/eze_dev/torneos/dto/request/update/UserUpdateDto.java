package com.eze_dev.torneos.dto.request.update;

import jakarta.validation.constraints.Email;

public record UserUpdateDto(
        @Email(message = "Email must be valid")
        String email,
        String actualPassword,
        String newPassword,
        String confirmNewPassword,
        PlayerUpdateDto player
) {}
