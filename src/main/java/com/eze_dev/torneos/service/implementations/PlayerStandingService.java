package com.eze_dev.torneos.service.implementations;

import com.eze_dev.torneos.dto.response.PaginatedResponseDto;
import com.eze_dev.torneos.dto.response.PlayerRankingResponseDto;
import com.eze_dev.torneos.dto.response.PlayerStandingResponseDto;
import com.eze_dev.torneos.dto.response.PlayerSummaryResponseDto;
import com.eze_dev.torneos.mapper.PlayerSummaryMapper;
import com.eze_dev.torneos.model.Player;
import com.eze_dev.torneos.model.Tournament;
import com.eze_dev.torneos.repository.PlayerRepository;
import com.eze_dev.torneos.repository.TournamentRepository;
import com.eze_dev.torneos.service.interfaces.IPlayerStandingService;
import com.eze_dev.torneos.strategy.tournament.PairStanding;
import com.eze_dev.torneos.strategy.tournament.TournamentStrategy;
import com.eze_dev.torneos.strategy.tournament.TournamentStrategyFactory;
import com.eze_dev.torneos.types.CategoryType;
import com.eze_dev.torneos.types.GenderType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerStandingService implements IPlayerStandingService {

    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentStrategyFactory tournamentStrategyFactory;
    private final PlayerSummaryMapper playerSummaryMapper;

    @Override
    public PlayerStandingResponseDto getPlayerStandingById(UUID playerId) {

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

        List<Tournament> playerTournaments = tournamentRepository.findTournamentsByPlayerId(playerId);

        int totalMatchesPlayed = 0;
        int totalMatchesWon = 0;
        int totalMatchesLost = 0;
        int totalGamesPlayed = 0;
        int totalGamesWon = 0;
        int totalGamesLost = 0;

        for (Tournament tournament : playerTournaments) {
            TournamentStrategy strategy = tournamentStrategyFactory.getStrategy(tournament.getTournamentType());
            List<PairStanding> standings = strategy.calculateStandings(tournament);

            Optional<PairStanding> playerPairStanding = standings.stream()
                    .filter(standing -> isPlayerInPair(standing, playerId))
                    .findFirst();

            if (playerPairStanding.isPresent()) {
                PairStanding pairStanding = playerPairStanding.get();

                totalMatchesPlayed += pairStanding.getMatchesPlayed();
                totalMatchesWon += pairStanding.getWins();
                totalMatchesLost += pairStanding.getLosses();
                totalGamesPlayed += (pairStanding.getGamesWon() + pairStanding.getGamesLost());
                totalGamesWon += pairStanding.getGamesWon();
                totalGamesLost += pairStanding.getGamesLost();
            }
        }

        double matchesEfficiency = totalMatchesPlayed > 0 ?
                (double) totalMatchesWon / totalMatchesPlayed * 100 : 0.0;
        double gamesEfficiency = totalGamesPlayed > 0 ?
                (double) totalGamesWon / totalGamesPlayed * 100 : 0.0;

        PlayerSummaryResponseDto playerSummary = playerSummaryMapper.toDto(player);

        return PlayerStandingResponseDto.builder()
                .playerSummary(playerSummary)
                .totalMatchesPlayed(totalMatchesPlayed)
                .totalMatchesWon(totalMatchesWon)
                .totalMatchesLost(totalMatchesLost)
                .matchesEfficiency(matchesEfficiency)
                .totalGamesPlayed(totalGamesPlayed)
                .totalGamesWon(totalGamesWon)
                .totalGamesLost(totalGamesLost)
                .gamesEfficiency(gamesEfficiency)
                .build();
    }

    @Override
    public List<PlayerStandingResponseDto> getAllPlayersStandings() {
        List<Player> allPlayers = playerRepository.findAll();

        return allPlayers.stream()
                .map(player -> getPlayerStandingById(player.getId()))
                .sorted((s1, s2) -> {
                    // Ordenar por eficiencia de partidos descendente
                    int efficiencyCompare = Double.compare(s2.getMatchesEfficiency(), s1.getMatchesEfficiency());

                    if (efficiencyCompare != 0) return efficiencyCompare;

                    // En caso de empate, ordenar por partidos ganados
                    return Integer.compare(s2.getTotalMatchesWon(), s1.getTotalMatchesWon());
                })
                .toList();
    }

    private boolean isPlayerInPair(PairStanding standing, UUID playerId) {
        return standing.getPair().getPlayer1().getId().equals(playerId) ||
                standing.getPair().getPlayer2().getId().equals(playerId);
    }
}