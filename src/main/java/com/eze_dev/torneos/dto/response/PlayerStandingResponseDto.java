package com.eze_dev.torneos.dto.response;

public record PlayerStandingResponseDto(
        PlayerSummaryResponseDto playerSummary,
        int totalMatchesPlayed,
        int totalMatchesWon,
        int totalMatchesLost,
        double matchesEfficiency,
        int totalGamesPlayed,
        int totalGamesWon,
        int totalGamesLost,
        double gamesEfficiency
) {}
