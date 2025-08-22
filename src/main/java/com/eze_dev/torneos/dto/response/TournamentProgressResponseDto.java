package com.eze_dev.torneos.dto.response;

import com.eze_dev.torneos.types.TournamentStatus;

public record TournamentProgressResponseDto(
        int totalMatches,
        int completedMatches,
        TournamentStatus status,
        double completionPercentage
) {}
