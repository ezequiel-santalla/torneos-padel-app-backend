package com.eze_dev.torneos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PairStandingResponseDto {

    private UUID pairId;
    private String pairName;
    private int matchesPlayed;
    private int wins;
    private int losses;
    private int gamesWon;
    private int gamesLost;
    private int points;
}
