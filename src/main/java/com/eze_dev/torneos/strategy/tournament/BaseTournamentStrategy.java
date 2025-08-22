package com.eze_dev.torneos.strategy.tournament;

import com.eze_dev.torneos.model.Match;
import com.eze_dev.torneos.model.Pair;
import com.eze_dev.torneos.model.Tournament;
import com.eze_dev.torneos.types.MatchStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class BaseTournamentStrategy implements TournamentStrategy {

    // Métodos abstractos que cada subclase debe implementar
    protected abstract TournamentConfig getConfig();

    // Métodos auxiliares de creación y análisis
    protected Match createMatch(Tournament tournament, Pair pair1, Pair pair2, int delayMinutes) {
        return Match.builder()
                .tournament(tournament)
                .pair1(pair1)
                .pair2(pair2)
                .status(MatchStatus.PENDING)
                .scheduledDate(tournament.getStartDate().plusMinutes(delayMinutes))
                .build();
    }

    protected Set<String> getPlayedCombinations(Tournament tournament) {
        Set<String> combinations = new HashSet<>();
        tournament.getMatches().forEach(match -> {
            combinations.add(match.getPair1().getId() + "-" + match.getPair2().getId());
            combinations.add(match.getPair2().getId() + "-" + match.getPair1().getId());
        });
        return combinations;
    }

    protected int getCompletedMatchesCount(Tournament tournament) {
        return (int) tournament.getMatches().stream()
                .filter(match -> match.getStatus() == MatchStatus.COMPLETED)
                .count();
    }

    protected List<Match> getCompletedMatches(Tournament tournament) {
        return tournament.getMatches().stream()
                .filter(match -> match.getStatus() == MatchStatus.COMPLETED)
                .toList();
    }

    protected Map<UUID, Long> countMatchesPerPair(Tournament tournament) {
        return tournament.getMatches().stream()
                .filter(match -> match.getStatus() == MatchStatus.COMPLETED)
                .flatMap(match -> Stream.of(match.getPair1().getId(), match.getPair2().getId()))
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()));
    }

    protected boolean canPlay(Pair pair1, Pair pair2, Set<String> playedCombinations) {
        String combo1 = pair1.getId() + "-" + pair2.getId();
        String combo2 = pair2.getId() + "-" + pair1.getId();
        return !playedCombinations.contains(combo1) && !playedCombinations.contains(combo2);
    }

    // Métodos de análisis de matches
    protected Pair getWinner(Match match) {
        int score1 = match.getPair1Score() != null ? match.getPair1Score() : 0;
        int score2 = match.getPair2Score() != null ? match.getPair2Score() : 0;
        return score1 > score2 ? match.getPair1() : match.getPair2();
    }

    protected Pair getLoser(Match match) {
        int score1 = match.getPair1Score() != null ? match.getPair1Score() : 0;
        int score2 = match.getPair2Score() != null ? match.getPair2Score() : 0;
        return score1 > score2 ? match.getPair2() : match.getPair1();
    }

    protected List<Pair> getWinners(List<Match> matches) {
        return matches.stream()
                .map(this::getWinner)
                .collect(Collectors.toList());
    }

    protected List<Pair> getLosers(List<Match> matches) {
        return matches.stream()
                .map(this::getLoser)
                .collect(Collectors.toList());
    }

    // Métodos de standings
    protected List<PairStanding> initializeStandings(Tournament tournament) {
        return tournament.getPairs().stream()
                .map(pair -> new PairStanding(pair, 0, 0, 0, 0, 0, 0))
                .collect(Collectors.toList());
    }

    protected void updateStandingsFromMatch(List<PairStanding> standings, Match match) {
        PairStanding ps1 = findStandingByPair(standings, match.getPair1());
        PairStanding ps2 = findStandingByPair(standings, match.getPair2());

        ps1.setMatchesPlayed(ps1.getMatchesPlayed() + 1);
        ps2.setMatchesPlayed(ps2.getMatchesPlayed() + 1);

        int score1 = match.getPair1Score() != null ? match.getPair1Score() : 0;
        int score2 = match.getPair2Score() != null ? match.getPair2Score() : 0;

        ps1.setGamesWon(ps1.getGamesWon() + score1);
        ps1.setGamesLost(ps1.getGamesLost() + score2);
        ps2.setGamesWon(ps2.getGamesWon() + score2);
        ps2.setGamesLost(ps2.getGamesLost() + score1);

        if (score1 > score2) {
            ps1.setWins(ps1.getWins() + 1);
            ps2.setLosses(ps2.getLosses() + 1);
        } else if (score2 > score1) {
            ps2.setWins(ps2.getWins() + 1);
            ps1.setLosses(ps1.getLosses() + 1);
        }
    }

    protected PairStanding findStandingByPair(List<PairStanding> standings, Pair pair) {
        return standings.stream()
                .filter(ps -> ps.getPair().getId().equals(pair.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Pair not found: " + pair.getId()));
    }

    protected void assignPoints(List<PairStanding> standings) {
        List<Integer> positionPoints = getConfig().positionPoints();
        for (int i = 0; i < standings.size() && i < positionPoints.size(); i++) {
            standings.get(i).setPoints(positionPoints.get(i));
        }
    }

    protected List<PairStanding> calculateCurrentStandings(Tournament tournament) {
        List<PairStanding> standings = initializeStandings(tournament);

        tournament.getMatches().stream()
                .filter(match -> match.getStatus() == MatchStatus.COMPLETED)
                .forEach(match -> updateStandingsFromMatch(standings, match));

        return standings;
    }

    protected void sortStandings(List<PairStanding> standings) {
        standings.sort((a, b) -> {
            // 1. Victories
            int compWins = Integer.compare(b.getWins(), a.getWins());
            if (compWins != 0) return compWins;

            // 2. Games difference
            int diffA = a.getGamesWon() - a.getGamesLost();
            int diffB = b.getGamesWon() - b.getGamesLost();
            int compDiff = Integer.compare(diffB, diffA);
            if (compDiff != 0) return compDiff;

            // 3. Games won
            return Integer.compare(b.getGamesWon(), a.getGamesWon());
        });
    }
}