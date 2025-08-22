package com.eze_dev.torneos.dto.request.create;

import com.eze_dev.torneos.types.GenderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserCreateDto(
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotNull(message = "Gender is required")
        GenderType genderType,

        @NotBlank(message = "DNI is required")
        @Pattern(regexp = "\\d{8}", message = "DNI must have 8 digits")
        String dni,

        @NotBlank(message = "Phone number is required")
        String phoneNumber
) {}
