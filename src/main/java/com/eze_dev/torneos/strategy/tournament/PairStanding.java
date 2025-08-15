package com.eze_dev.torneos.strategy.tournament;

import com.eze_dev.torneos.model.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PairStanding {

    private Pair pair;
    private int matchesPlayed;
    private int wins;
    private int losses;
    private int gamesWon;
    private int gamesLost;
    private int points;
}

