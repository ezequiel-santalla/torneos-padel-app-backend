package com.eze_dev.torneos.strategy.tournament;

import com.eze_dev.torneos.model.Match;
import com.eze_dev.torneos.model.Pair;
import com.eze_dev.torneos.model.Tournament;
import com.eze_dev.torneos.types.MatchStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class QuadrangularStrategy extends BaseTournamentStrategy {

    private static final TournamentConfig CONFIG = TournamentConfig.quadrangular();

    @Override
    protected TournamentConfig getConfig() {
        return CONFIG;
    }

    @Override
    public void validateBeforeStart(Tournament tournament) {
        if (tournament.getPairs().size() != CONFIG.requiredPairs()) {
            throw new IllegalStateException("A quadrangular tournament must have exactly 4 pairs.");
        }
    }

    @Override
    public List<Match> generateMatches(Tournament tournament) {
        List<Pair> shuffledPairs = new ArrayList<>(tournament.getPairs());
        Collections.shuffle(shuffledPairs);

        List<Match> matches = new ArrayList<>();

        for (int i = 0; i < shuffledPairs.size(); i += 2) {
            matches.add(createMatch(
                    tournament,
                    shuffledPairs.get(i),
                    shuffledPairs.get(i + 1),
                    CONFIG.getFirstRoundDelay()));
        }

        log.info("Generated {} randomized initial matches for quadrangular tournament", matches.size());
        return matches;
    }

    @Override
    public List<Match> processMatchCompletionAndAdvance(Tournament tournament) {
        log.debug("Processing match completion for tournament {}", tournament.getId());

        int completedCount = getCompletedMatchesCount(tournament);
        int totalMatches = tournament.getMatches().size();

        // 2nd round: Winners vs Losers (after 2 matches)
        if (completedCount == CONFIG.firstRoundMatches() && totalMatches == CONFIG.firstRoundMatches()) {
            log.info("Generating second round matches for tournament {}", tournament.getId());
            return generateSecondRound(tournament);
        }

        // 3rd round: Final matches (after 4 matches)
        if (completedCount == CONFIG.secondRoundMatches() && totalMatches == CONFIG.secondRoundMatches()) {
            log.info("Generating third round matches for tournament {}", tournament.getId());
            return generateThirdRound(tournament);
        }

        log.debug("No new rounds to generate for tournament {}", tournament.getId());
        return new ArrayList<>();
    }

    @Override
    public boolean isComplete(Tournament tournament) {
        if (tournament.getMatches().size() != CONFIG.totalMatches()) {
            log.debug("Tournament {} incomplete: {} matches (expected {})",
                    tournament.getId(), tournament.getMatches().size(), CONFIG.totalMatches());
            return false;
        }

        boolean allCompleted = tournament.getMatches().stream()
                .allMatch(m -> m.getStatus() == MatchStatus.COMPLETED);

        if (allCompleted) {
            Map<UUID, Long> matchCounts = countMatchesPerPair(tournament);
            boolean validStructure = matchCounts.values().stream()
                    .allMatch(count -> count == CONFIG.matchesPerPair());

            if (!validStructure) {
                log.warn("Tournament {} has invalid structure: {}",
                        tournament.getId(), matchCounts);
            }

            return validStructure;
        }

        return false;
    }

    @Override
    public List<PairStanding> calculateStandings(Tournament tournament) {
        List<PairStanding> standings = calculateCurrentStandings(tournament);

        // Apply sorting based on tournament state
        if (isComplete(tournament)) {
            // For completed tournaments, use standard sorting
            sortStandings(standings);
            assignPoints(standings);
        } else {
            sortStandings(standings);
        }

        return standings;
    }

    // Métodos específicos de QuadrangularStrategy
    private List<Match> generateSecondRound(Tournament tournament) {
        List<Match> completedMatches = getCompletedMatches(tournament);

        if (completedMatches.size() != CONFIG.firstRoundMatches()) {
            throw new IllegalStateException("Exactly " + CONFIG.firstRoundMatches() + " completed matches are needed for the second round");
        }

        List<Pair> winners = getWinners(completedMatches);
        List<Pair> losers = getLosers(completedMatches);
        Set<String> playedCombinations = getPlayedCombinations(tournament);

        List<Match> newMatches = new ArrayList<>();
        boolean[] usedLosers = new boolean[losers.size()];

        for (Pair winner : winners) {
            boolean matched = false;

            for (int i = 0; i < losers.size(); i++) {
                if (!usedLosers[i] && canPlay(winner, losers.get(i), playedCombinations)) {
                    newMatches.add(createMatch(tournament, winner, losers.get(i), CONFIG.getSecondRoundDelay()));
                    usedLosers[i] = true;
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                throw new IllegalStateException("Cannot match winner " + winner.getId());
            }
        }

        log.info("Generated second round matches: {}",
                newMatches.stream()
                        .map(m -> m.getPair1().getId() + " vs " + m.getPair2().getId())
                        .collect(Collectors.joining(", ")));

        return newMatches;
    }

    private List<Match> generateThirdRound(Tournament tournament) {
        Set<String> playedCombinations = getPlayedCombinations(tournament);
        List<Pair> pairs = tournament.getPairs();
        List<Match> newMatches = new ArrayList<>();

        for (int i = 0; i < pairs.size() && newMatches.size() < 2; i++) {
            for (int j = i + 1; j < pairs.size() && newMatches.size() < 2; j++) {
                if (canPlay(pairs.get(i), pairs.get(j), playedCombinations)) {
                    newMatches.add(createMatch(tournament, pairs.get(i), pairs.get(j), CONFIG.getThirdRoundDelay()));
                    log.debug("Third round match created: {} vs {}",
                            pairs.get(i).getId(), pairs.get(j).getId());
                }
            }
        }

            if (newMatches.size() != CONFIG.matchesPerRound()) {
            log.warn("Expected 2 matches for third round, but generated {}", newMatches.size());
        }

        log.info("Generated {} final round matches", newMatches.size());
        return newMatches;
    }
}