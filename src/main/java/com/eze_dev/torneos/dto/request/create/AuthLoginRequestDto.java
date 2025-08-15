package com.eze_dev.torneos.dto.request.create;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDto(
        @NotBlank String email,
        @NotBlank String password) {}
