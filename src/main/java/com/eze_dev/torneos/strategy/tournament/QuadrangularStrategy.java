package com.eze_dev.torneos.strategy.tournament;

import com.eze_dev.torneos.model.Match;
import com.eze_dev.torneos.model.Pair;
import com.eze_dev.torneos.model.Tournament;
import com.eze_dev.torneos.types.MatchStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuadrangularStrategy implements TournamentStrategy {

    @Override
    public void validateBeforeStart(Tournament tournament) {
        if (tournament.getPairs().size() != 4) {
            throw new IllegalStateException("A quadrangular tournament must have exactly 4 pairs.");
        }
    }

    @Override
    public List<Match> generateMatches(Tournament tournament) {
        List<Pair> pairs = tournament.getPairs();
        List<Match> matches = new ArrayList<>();

        LocalDateTime baseTime = tournament.getStartDate() != null
                ? tournament.getStartDate().toLocalDate().atStartOfDay()
                : LocalDateTime.now();

        int matchIndex = 0;
        for (int i = 0; i < pairs.size(); i++) {
            for (int j = i + 1; j < pairs.size(); j++) {
                matches.add(Match.builder()
                        .tournament(tournament)
                        .pair1(pairs.get(i))
                        .pair2(pairs.get(j))
                        .status(MatchStatus.PENDING)
                        .scheduledDate(baseTime.plusHours(matchIndex))
                        .build());
                matchIndex++;
            }
        }
        return matches;
    }

    @Override
    public List<PairStanding> calculateStandings(Tournament tournament) {
        List<PairStanding> standings = new ArrayList<>();

        for (Pair pair : tournament.getPairs()) {
            standings.add(new PairStanding(pair, 0, 0, 0, 0, 0, 0));
        }

        for (Match match : tournament.getMatches()) {
            if (match.getStatus() != MatchStatus.COMPLETED) {
                continue;
            }

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

        standings.sort((a, b) -> {
            int compWins = Integer.compare(b.getWins(), a.getWins());
            if (compWins != 0) return compWins;

            int diffA = a.getGamesWon() - a.getGamesLost();
            int diffB = b.getGamesWon() - b.getGamesLost();
            return Integer.compare(diffB, diffA);
        });

        boolean tournamentFinished = isTournamentFinished(tournament);

        assignPoints(standings, tournamentFinished);

        return standings;
    }

    private boolean isTournamentFinished(Tournament tournament) {
        List<Match> matches = tournament.getMatches();

        if (matches.isEmpty()) {
            return false;
        }

        return matches.stream()
                .allMatch(match -> match.getStatus() == MatchStatus.COMPLETED);
    }

    private void assignPoints(List<PairStanding> standings, boolean tournamentFinished) {
        for (int i = 0; i < standings.size(); i++) {
            int points;

            if (tournamentFinished) {
                points = switch (i) {
                    case 0 -> 10;
                    case 1 -> 6;
                    case 2 -> 3;
                    default -> 1;
                };
            } else {
                points = 0;
            }

            standings.get(i).setPoints(points);
        }
    }

    private PairStanding findStandingByPair(List<PairStanding> standings, Pair pair) {
        return standings.stream()
                .filter(ps -> ps.getPair().getId().equals(pair.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Pair not found in standings"));
    }
}

