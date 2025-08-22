package com.eze_dev.torneos.strategy.tournament;

import java.util.List;

public record TournamentConfig(
        int requiredPairs,
        int totalMatches,
        int matchesPerRound,
        int matchesPerPair,
        int firstRoundMatches,
        int secondRoundMatches,
        List<Integer> roundDelays,
        List<Integer> positionPoints
) {

    public static TournamentConfig quadrangular() {
        return new TournamentConfig(
                4,  // requiredPairs
                6,  // totalMatches
                2,  // matchesPerRound
                3,  // matchesPerPair
                2,  // firstRoundMatches
                4,  // secondRoundMatches
                List.of(0, 40, 80),  // roundDelays
                List.of(10, 6, 3, 1) // positionPoints
        );
    }

    public static TournamentConfig hexagonal() {
        return new TournamentConfig(
                6,  // requiredPairs
                9,  // totalMatches
                3,  // matchesPerRound
                3,  // matchesPerPair
                3,  // firstRoundMatches
                6,  // secondRoundMatches
                List.of(0, 40, 80),  // roundDelays
                List.of(15, 12, 9, 6, 3, 1) // positionPoints
        );
    }

    public int getFirstRoundDelay() {
        return roundDelays.getFirst();
    }

    public int getSecondRoundDelay() {
        return roundDelays.get(1);
    }

    public int getThirdRoundDelay() {
        return roundDelays.get(2);
    }
}