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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerRankingService implements IPlayerRankingService {

    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentStrategyFactory tournamentStrategyFactory;

    @Override
    public List<PlayerRankingResponseDto> getPlayerRankings(CategoryType category, GenderType gender) {
        List<Player> playersInCategory = playerRepository.findPlayersWhoPlayedInCategoryAndGender(category, gender);

        List<PlayerRankingResponseDto> rankings = playersInCategory.stream()
                .map(player -> getPlayerPointsRanking(player.getId(), category, gender))
                .sorted(Comparator.comparingInt(PlayerRankingResponseDto::totalPoints).reversed())
                .toList();

        return assignPositions(rankings);
    }

    @Override
    public PaginatedResponseDto<PlayerRankingResponseDto> getPlayerRankingsPaginated(CategoryType category, GenderType gender, Pageable pageable) {
        Page<Player> rankingsPage = playerRepository.findPlayersWhoPlayedInCategoryAndGender(category, gender, pageable);

        List<PlayerRankingResponseDto> pageRankings = rankingsPage.getContent()
                .stream()
                .map(player -> getPlayerPointsRanking(player.getId(), category, gender))
                .sorted(Comparator.comparingInt(PlayerRankingResponseDto::totalPoints).reversed())
                .toList();

        List<PlayerRankingResponseDto> rankingsWithPositions = assignPositionsForPagination(
                pageRankings, category, gender);

        return new PaginatedResponseDto<>(rankingsWithPositions, rankingsPage);
    }

    private PlayerRankingResponseDto getPlayerPointsRanking(UUID playerId, CategoryType category, GenderType gender) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

        List<Tournament> playerTournaments = tournamentRepository.findTournamentsByPlayerId(playerId);

        List<Tournament> filteredTournaments = playerTournaments.stream()
                .filter(tournament ->
                        tournament.getCategoryType().equals(category) &&
                                tournament.getGenderType().equals(gender)
                )
                .toList();

        int totalPoints = filteredTournaments.stream()
                .mapToInt(tournament -> {
                    TournamentStrategy strategy = tournamentStrategyFactory.getStrategy(tournament.getTournamentType());
                    List<PairStanding> standings = strategy.calculateStandings(tournament);

                    return standings.stream()
                            .filter(standing -> isPlayerInPair(standing, playerId))
                            .findFirst()
                            .map(PairStanding::getPoints)
                            .orElse(0);
                })
                .sum();

        int tournamentsPlayed = filteredTournaments.size();

        return new PlayerRankingResponseDto(
                playerId,
                player.getName(),
                player.getLastName(),
                player.getGenderType(),
                0,
                totalPoints,
                tournamentsPlayed
        );
    }

    private List<PlayerRankingResponseDto> assignPositions(List<PlayerRankingResponseDto> rankings) {
        if (rankings.isEmpty()) {
            return rankings;
        }

        int currentPosition = 1;
        int previousPoints = -1;
        List<PlayerRankingResponseDto> result = new ArrayList<>();

        for (int i = 0; i < rankings.size(); i++) {
            PlayerRankingResponseDto ranking = rankings.get(i);

            if (i == 0 || ranking.totalPoints() != previousPoints) {
                currentPosition = i + 1;
            }

            PlayerRankingResponseDto withPosition = new PlayerRankingResponseDto(
                    ranking.id(),
                    ranking.name(),
                    ranking.lastName(),
                    ranking.genderType(),
                    currentPosition,
                    ranking.totalPoints(),
                    ranking.tournamentsPlayed()
            );

            result.add(withPosition);
            previousPoints = ranking.totalPoints();
        }

        return result;
    }

    private List<PlayerRankingResponseDto> assignPositionsForPagination(
            List<PlayerRankingResponseDto> pageRankings,
            CategoryType category,
            GenderType gender) {

        List<PlayerRankingResponseDto> allRankings = getPlayerRankings(category, gender);

        Map<UUID, Integer> positionMap = allRankings.stream()
                .collect(Collectors.toMap(
                        PlayerRankingResponseDto::id,
                        PlayerRankingResponseDto::position
                ));

        return pageRankings.stream()
                .map(ranking -> new PlayerRankingResponseDto(
                        ranking.id(),
                        ranking.name(),
                        ranking.lastName(),
                        ranking.genderType(),
                        positionMap.get(ranking.id()),
                        ranking.totalPoints(),
                        ranking.tournamentsPlayed()
                ))
                .toList();
    }

    private boolean isPlayerInPair(PairStanding standing, UUID playerId) {
        return standing.getPair().getPlayer1().getId().equals(playerId) ||
                standing.getPair().getPlayer2().getId().equals(playerId);
    }
}