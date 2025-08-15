package com.eze_dev.torneos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerStandingResponseDto {

    private PlayerSummaryResponseDto playerSummary;
    private int totalMatchesPlayed;
    private int totalMatchesWon;
    private int totalMatchesLost;
    private double matchesEfficiency;
    private int totalGamesPlayed;
    private int totalGamesWon;
    private int totalGamesLost;
    private double gamesEfficiency;
}
