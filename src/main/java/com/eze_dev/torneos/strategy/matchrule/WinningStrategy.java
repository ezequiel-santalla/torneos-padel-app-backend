package com.eze_dev.torneos.strategy.matchrule;

import com.eze_dev.torneos.model.Match;
import com.eze_dev.torneos.model.Pair;

import java.util.Optional;

public interface WinningStrategy {

    boolean hasWinner(Match match);
    Optional<Pair> getWinner(Match match);
    boolean isValidResult(Match match);
}
