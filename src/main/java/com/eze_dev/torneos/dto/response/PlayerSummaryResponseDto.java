package com.eze_dev.torneos.dto.response;

import java.util.UUID;

public record PlayerSummaryResponseDto(
        UUID id,
        String name,
        String lastName
) {}
