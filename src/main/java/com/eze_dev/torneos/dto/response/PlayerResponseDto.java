package com.eze_dev.torneos.dto.response;

import com.eze_dev.torneos.types.GenderType;

import java.util.UUID;

public record PlayerResponseDto(
        UUID id,
        String name,
        String lastName,
        GenderType genderType,
        String dni,
        String phoneNumber
) {}
