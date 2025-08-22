package com.eze_dev.torneos.strategy.tournament;

import com.eze_dev.torneos.model.Match;
import com.eze_dev.torneos.model.Tournament;

import java.util.List;

public interface TournamentStrategy {
    void validateBeforeStart(Tournament tournament);
    List<Match> generateMatches(Tournament tournament);
    List<Match> processMatchCompletionAndAdvance(Tournament tournament);
    boolean isComplete(Tournament tournament);
    List<PairStanding> calculateStandings(Tournament tournament);
}