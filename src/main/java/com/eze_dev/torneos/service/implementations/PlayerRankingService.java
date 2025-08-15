package com.eze_dev.torneos.service.implementations;

import com.eze_dev.torneos.dto.response.PaginatedResponseDto;
import com.eze_dev.torneos.dto.response.PlayerRankingResponseDto;
import com.eze_dev.torneos.model.Player;
import com.eze_dev.torneos.model.Tournament;
import com.eze_dev.torneos.repository.PlayerRepository;
import com.eze_dev.torneos.repository.TournamentRepository;
import com.eze_dev.torneos.service.interfaces.IPlayerRankingService;
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
public class PlayerRankingService implements IPlayerRankingService {

    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentStrategyFactory tournamentStrategyFactory;

    @Override
    public List<PlayerRankingResponseDto> getPlayerRankings(CategoryType category, GenderType gender) {
        List<Player> allPlayers = playerRepository.findAll();

        return allPlayers.stream()
                .filter(player -> hasPlayedInCategoryAndGender(player.getId(), category, gender))
                .map(player -> getPlayerPointsRanking(player.getId(), category, gender))
                .sorted((p1, p2) -> Integer.compare(p2.getTotalPoints(), p1.getTotalPoints()))
                .toList();
    }

    @Override
    public PaginatedResponseDto<PlayerRankingResponseDto> getPlayerRankingsPaginated(CategoryType category, GenderType gender, Pageable pageable) {
        Page<Player> rankingsPage = playerRepository.findPlayersWhoPlayedInCategoryAndGender(category, gender, pageable);

        List<PlayerRankingResponseDto> rankings = rankingsPage.getContent()
                .stream()
                .map(ranking -> getPlayerPointsRanking(ranking.getId(), category, gender))
                .sorted((p1, p2) -> Integer.compare(p2.getTotalPoints(), p1.getTotalPoints()))
                .toList();

        return new PaginatedResponseDto<>(rankings, rankingsPage);
    }

    private boolean hasPlayedInCategoryAndGender(UUID playerId, CategoryType category, GenderType gender) {
        List<Tournament> playerTournaments = tournamentRepository.findTournamentsByPlayerId(playerId);

        return playerTournaments.stream()
                .anyMatch(tournament ->
                        (category == null || tournament.getCategoryType().equals(category)) &&
                                (gender == null || tournament.getGenderType().equals(gender))
                );
    }


    private PlayerRankingResponseDto getPlayerPointsRanking(UUID playerId, CategoryType category, GenderType gender) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

        List<Tournament> playerTournaments = tournamentRepository.findTournamentsByPlayerId(playerId);

        List<Tournament> filteredTournaments = playerTournaments.stream()
                .filter(tournament ->
                        (category == null || tournament.getCategoryType().equals(category)) &&
                                (gender == null || tournament.getGenderType().equals(gender))
                )
                .toList();

        int totalPoints = 0;

        for (Tournament tournament : filteredTournaments) {
            TournamentStrategy strategy = tournamentStrategyFactory.getStrategy(tournament.getTournamentType());
            List<PairStanding> standings = strategy.calculateStandings(tournament);

            Optional<PairStanding> playerPairStanding = standings.stream()
                    .filter(standing -> isPlayerInPair(standing, playerId))
                    .findFirst();

            if (playerPairStanding.isPresent()) {
                totalPoints += playerPairStanding.get().getPoints();
            }
        }

        int tournamentsPlayed = getTournamentsPlayedCount(playerId, category, gender);

        return PlayerRankingResponseDto.builder()
                .id(playerId)
                .name(player.getName())
                .lastName(player.getLastName())
                .genderType(player.getGenderType())
                .totalPoints(totalPoints)
                .tournamentsPlayed(tournamentsPlayed)
                .build();
    }

    private int getTournamentsPlayedCount(UUID playerId, CategoryType category, GenderType gender) {
        List<Tournament> playerTournaments = tournamentRepository.findTournamentsByPlayerId(playerId);

        return (int) playerTournaments.stream()
                .filter(tournament ->
                        (category == null || tournament.getCategoryType().equals(category)) &&
                                (gender == null || tournament.getGenderType().equals(gender))
                )
                .count();
    }

    private boolean isPlayerInPair(PairStanding standing, UUID playerId) {
        return standing.getPair().getPlayer1().getId().equals(playerId) ||
                standing.getPair().getPlayer2().getId().equals(playerId);
    }
}
