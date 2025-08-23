package com.eze_dev.torneos.dto.response;

import com.eze_dev.torneos.types.GenderType;

import java.util.UUID;

public record PlayerSummaryResponseDto(
        UUID id,
        String name,
        String lastName,
        GenderType genderType
) {}
