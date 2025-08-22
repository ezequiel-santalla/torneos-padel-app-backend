package com.eze_dev.torneos.dto.response;

import java.util.UUID;

public record PairStandingResponseDto(
        UUID pairId,
        String pairName,
        int matchesPlayed,
        int wins,
        int losses,
        int gamesWon,
        int gamesLost,
        int points
) {}
