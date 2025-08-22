package com.eze_dev.torneos.dto.response;

import com.eze_dev.torneos.types.TournamentStatus;

import java.util.UUID;

public record TournamentSummaryResponseDto(
        UUID id,
        String name,
        TournamentStatus status,
        int pairCount,
        int matchCount
) {}
