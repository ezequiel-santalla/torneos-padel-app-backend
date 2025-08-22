package com.eze_dev.torneos.strategy.tournament;

import com.eze_dev.torneos.model.Match;
import com.eze_dev.torneos.model.Pair;
import com.eze_dev.torneos.model.Tournament;
import com.eze_dev.torneos.types.MatchStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class HexagonalStrategy extends BaseTournamentStrategy {

    private static final TournamentConfig CONFIG = TournamentConfig.hexagonal();

    @Override
    protected TournamentConfig getConfig() {
        return CONFIG;
    }

    @Override
    public void validateBeforeStart(Tournament tournament) {
        if (tournament.getPairs().size() != CONFIG.requiredPairs()) {
            throw new IllegalStateException("A hexagonal tournament must have exactly 6 pairs.");
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

        log.info("Generated {} randomized initial matches for hexagonal tournament", matches.size());
        return matches;
    }

    @Override
    public List<Match> processMatchCompletionAndAdvance(Tournament tournament) {
        log.debug("Processing match completion for tournament {}", tournament.getId());

        int completedCount = getCompletedMatchesCount(tournament);
        int totalMatches = tournament.getMatches().size();

        // 2nd round: Winners vs Losers by proximity (after 3 matches)
        if (completedCount == CONFIG.firstRoundMatches() && totalMatches == CONFIG.firstRoundMatches()) {
            log.info("Generating second round matches for tournament {}", tournament.getId());
            return generateSecondRound(tournament);
        }

        // 3rd round: Final matches by ranking (after 6 matches)
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
            applyAdvancedTieBreaking(standings, tournament);
            assignPoints(standings);
        } else {
            sortStandings(standings);
        }

        return standings;
    }

    // Métodos específicos de HexagonalStrategy
    private List<Match> generateSecondRound(Tournament tournament) {
        List<Match> completedMatches = getCompletedMatches(tournament);

        if (completedMatches.size() != CONFIG.firstRoundMatches()) {
            throw new IllegalStateException("Exactly " + CONFIG.firstRoundMatches() + " completed matches are needed for the second round");
        }

        List<Pair> winners = getWinners(completedMatches);
        List<Pair> losers = getLosers(completedMatches);

        // ARREGLO: Usar calculateCurrentStandings que maneja todas las parejas correctamente
        List<PairStanding> allStandings = calculateCurrentStandings(tournament);

        // Filtrar ganadores y perdedores de los standings completos
        List<PairStanding> winnerStandings = allStandings.stream()
                .filter(standing -> winners.contains(standing.getPair()))
                .collect(Collectors.toList());

        List<PairStanding> loserStandings = allStandings.stream()
                .filter(standing -> losers.contains(standing.getPair()))
                .collect(Collectors.toList());

        // Ordenar por performance (diferencia de games, games ganados)
        sortStandingsByPerformance(winnerStandings);
        sortStandingsByPerformance(loserStandings);

        Set<String> playedCombinations = getPlayedCombinations(tournament);

        // Usar backtracking para garantizar emparejamientos válidos por cercanía
        List<Match> newMatches = createWinnersVsLosersMatches(tournament, winnerStandings, loserStandings, playedCombinations);

        log.info("Generated second round matches (winners vs losers by proximity): {}",
                newMatches.stream()
                        .map(m -> m.getPair1().getId() + " vs " + m.getPair2().getId())
                        .collect(Collectors.joining(", ")));

        return newMatches;
    }

    private List<Match> generateThirdRound(Tournament tournament) {
        // Calcular standings actuales y ordenar por ranking
        List<PairStanding> standings = calculateCurrentStandings(tournament);
        sortStandings(standings); // 1°, 2°, 3°, 4°, 5°, 6°

        Set<String> playedCombinations = getPlayedCombinations(tournament);

        // Crear matches priorizando cercanía de ranking
        List<Match> newMatches = createFinalRoundByRanking(tournament, standings, playedCombinations);

        log.info("Generated third round matches (by ranking): {}",
                newMatches.stream()
                        .map(m -> m.getPair1().getId() + " vs " + m.getPair2().getId())
                        .collect(Collectors.joining(", ")));

        return newMatches;
    }

    private List<Match> createWinnersVsLosersMatches(Tournament tournament,
                                                     List<PairStanding> winnerStandings,
                                                     List<PairStanding> loserStandings,
                                                     Set<String> playedCombinations) {
        List<Match> matches = new ArrayList<>();

        // Algoritmo de backtracking optimizado para cercanía
        if (matchWinnersWithLosers(tournament, new ArrayList<>(winnerStandings), new ArrayList<>(loserStandings),
                playedCombinations, matches, 0)) {
            return matches;
        }

        throw new IllegalStateException("Failed to create winners vs losers matches - this should never happen mathematically");
    }

    private boolean matchWinnersWithLosers(Tournament tournament,
                                           List<PairStanding> winners,
                                           List<PairStanding> losers,
                                           Set<String> playedCombinations,
                                           List<Match> currentMatches,
                                           int winnerIndex) {

        if (winnerIndex >= winners.size()) {
            return true; // Todos los ganadores han sido emparejados
        }

        PairStanding currentWinner = winners.get(winnerIndex);

        // Crear set de perdedores ya usados en matches actuales
        Set<UUID> usedLoserIds = currentMatches.stream()
                .flatMap(match -> Stream.of(match.getPair1().getId(), match.getPair2().getId()))
                .collect(Collectors.toSet());

        // Intentar emparejar con perdedores disponibles
        for (PairStanding currentLoser : losers) {
            if (usedLoserIds.contains(currentLoser.getPair().getId())) {
                continue; // Ya usado
            }

            if (canPlay(currentWinner.getPair(), currentLoser.getPair(), playedCombinations)) {
                // Crear emparejamiento
                Match match = createMatch(tournament, currentWinner.getPair(), currentLoser.getPair(), CONFIG.getSecondRoundDelay());
                currentMatches.add(match);

                // Recursión con siguiente ganador
                if (matchWinnersWithLosers(tournament, winners, losers, playedCombinations, currentMatches, winnerIndex + 1)) {
                    return true;
                }

                // Backtrack
                currentMatches.removeLast();
            }
        }

        return false;
    }

    private List<Match> createFinalRoundByRanking(Tournament tournament,
                                                  List<PairStanding> standings,
                                                  Set<String> playedCombinations) {
        List<Match> matches = new ArrayList<>();
        List<PairStanding> available = new ArrayList<>(standings);

        // Algoritmo de backtracking que prioriza cercanía de ranking
        if (createRankingBasedMatches(tournament, available, playedCombinations, matches, standings, 0)) {
            return matches;
        }

        throw new IllegalStateException("Failed to create final round matches - this should never happen mathematically");
    }

    private boolean createRankingBasedMatches(Tournament tournament,
                                              List<PairStanding> available,
                                              Set<String> playedCombinations,
                                              List<Match> currentMatches,
                                              List<PairStanding> originalStandings,
                                              int depth) {

        if (currentMatches.size() >= CONFIG.matchesPerRound()) {
            return true; // Se completaron todos los matches necesarios
        }

        if (available.size() < 2) {
            return false; // No hay suficientes equipos disponibles
        }

        // Priorizar emparejamientos por cercanía de ranking
        // Intentar primero con los equipos más cercanos en ranking
        for (int i = 0; i < available.size() - 1; i++) {
            PairStanding pair1 = available.get(i);

            // Buscar el oponente más cercano en ranking
            for (int j = i + 1; j < available.size(); j++) {
                PairStanding pair2 = available.get(j);

                if (canPlay(pair1.getPair(), pair2.getPair(), playedCombinations)) {
                    // Crear emparejamiento
                    Match match = createMatch(tournament, pair1.getPair(), pair2.getPair(), CONFIG.getThirdRoundDelay());
                    currentMatches.add(match);

                    // Crear nueva lista sin las parejas usadas
                    List<PairStanding> remainingAvailable = new ArrayList<>(available);
                    remainingAvailable.remove(pair1);
                    remainingAvailable.remove(pair2);

                    // Recursión
                    if (createRankingBasedMatches(tournament, remainingAvailable, playedCombinations, currentMatches, originalStandings, depth + 1)) {
                        int rank1 = findRankInStandings(pair1, originalStandings);
                        int rank2 = findRankInStandings(pair2, originalStandings);
                        log.debug("Created final match: {}° vs {}° (ranking difference: {})",
                                rank1, rank2, Math.abs(rank1 - rank2));
                        return true;
                    }

                    // Backtrack
                    currentMatches.removeLast();
                }
            }
        }

        return false;
    }

    private int findRankInStandings(PairStanding pairStanding, List<PairStanding> standings) {
        for (int i = 0; i < standings.size(); i++) {
            if (standings.get(i).getPair().getId().equals(pairStanding.getPair().getId())) {
                return i + 1; // +1 porque el ranking empieza en 1, no en 0
            }
        }
        return -1; // No encontrado (no debería pasar)
    }

    private void sortStandingsByPerformance(List<PairStanding> standings) {
        standings.sort((a, b) -> {
            // 1. Diferencia de games (mejor performance primero)
            int diffA = a.getGamesWon() - a.getGamesLost();
            int diffB = b.getGamesWon() - b.getGamesLost();
            int compDiff = Integer.compare(diffB, diffA);
            if (compDiff != 0) return compDiff;

            // 2. Games ganados
            return Integer.compare(b.getGamesWon(), a.getGamesWon());
        });
    }

    private void applyAdvancedTieBreaking(List<PairStanding> standings, Tournament tournament) {
        standings.sort((a, b) -> {
            // 1. Victorias
            int compWins = Integer.compare(b.getWins(), a.getWins());
            if (compWins != 0) return compWins;

            // 2. Enfrentamiento directo (solo para empates de 2 equipos)
            int directResult = resolveDirectMatch(a, b, standings, tournament);
            if (directResult != 0) return directResult;

            // 3. Diferencia de games
            int diffA = a.getGamesWon() - a.getGamesLost();
            int diffB = b.getGamesWon() - b.getGamesLost();
            int compDiff = Integer.compare(diffB, diffA);
            if (compDiff != 0) return compDiff;

            // 4. Games ganados
            return Integer.compare(b.getGamesWon(), a.getGamesWon());
        });
    }

    private int resolveDirectMatch(PairStanding a, PairStanding b,
                                   List<PairStanding> allStandings, Tournament tournament) {
        // Solo aplicar si hay exactamente 2 equipos empatados en victorias
        long teamsWithSameWins = allStandings.stream()
                .mapToInt(PairStanding::getWins)
                .filter(wins -> wins == a.getWins())
                .count();

        if (teamsWithSameWins == 2) {
            return tournament.getMatches().stream()
                    .filter(match -> match.getStatus() == MatchStatus.COMPLETED)
                    .filter(match -> isDirectMatch(match, a.getPair(), b.getPair()))
                    .findFirst()
                    .map(match -> {
                        Pair winner = getWinner(match);
                        if (winner.getId().equals(a.getPair().getId())) return -1; // a gana
                        if (winner.getId().equals(b.getPair().getId())) return 1;  // b gana
                        return 0;
                    })
                    .orElse(0);
        }

        return 0;
    }

    private boolean isDirectMatch(Match match, Pair pair1, Pair pair2) {
        return (match.getPair1().getId().equals(pair1.getId()) &&
                match.getPair2().getId().equals(pair2.getId())) ||
                (match.getPair1().getId().equals(pair2.getId()) &&
                        match.getPair2().getId().equals(pair1.getId()));
    }
}