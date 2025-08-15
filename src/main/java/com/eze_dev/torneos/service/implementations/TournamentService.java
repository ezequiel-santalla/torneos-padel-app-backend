package com.eze_dev.torneos.service.implementations;

import com.eze_dev.torneos.dto.request.create.TournamentCreateDto;
import com.eze_dev.torneos.dto.response.*;
import com.eze_dev.torneos.dto.request.update.TournamentStatusUpdateDto;
import com.eze_dev.torneos.dto.request.update.TournamentUpdateDto;
import com.eze_dev.torneos.mapper.MatchMapper;
import com.eze_dev.torneos.mapper.PairMapper;
import com.eze_dev.torneos.mapper.PairStandingMapper;
import com.eze_dev.torneos.mapper.TournamentMapper;
import com.eze_dev.torneos.model.Match;
import com.eze_dev.torneos.model.Pair;
import com.eze_dev.torneos.model.Tournament;
import com.eze_dev.torneos.repository.PairRepository;
import com.eze_dev.torneos.repository.TournamentRepository;
import com.eze_dev.torneos.service.interfaces.ITournamentService;
import com.eze_dev.torneos.strategy.tournament.PairStanding;
import com.eze_dev.torneos.strategy.tournament.TournamentStrategy;
import com.eze_dev.torneos.strategy.tournament.TournamentStrategyFactory;
import com.eze_dev.torneos.types.MatchStatus;
import com.eze_dev.torneos.types.TournamentStatus;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentService implements ITournamentService {

    // Constantes para evitar duplicación de literales
    private static final String TOURNAMENT_NOT_FOUND_MSG = "Tournament not found with ID: ";
    private static final String PAIR_NOT_FOUND_MSG = "Pair not found with ID: ";
    private static final String TOURNAMENT_NAME_EXISTS_MSG = "Tournament with name %s already exists.";
    private static final String PAIR_ALREADY_ADDED_MSG = "Pair is already added to the tournament";
    private static final String PAIR_NOT_IN_TOURNAMENT_MSG = "Pair is not part of the tournament";
    private static final String TOURNAMENT_START_VALIDATION_MSG = "Solo se puede iniciar un torneo que esté en estado CREATED.";
    private static final String FINISHED_TOURNAMENT_UPDATE_MSG = "Cannot change status of a finished tournament.";
    private static final String INVALID_STATUS_TRANSITION_MSG = "Invalid status transition from %s to %s";

    private final TournamentRepository tournamentRepository;
    private final PairRepository pairRepository;
    private final TournamentMapper tournamentMapper;
    private final PairMapper pairMapper;
    private final MatchMapper matchMapper;
    private final PairStandingMapper pairStandingMapper;
    private final TournamentStrategyFactory tournamentStrategyFactory;

    @Override
    public TournamentResponseDto create(TournamentCreateDto tournamentCreateDto) {
        if (tournamentRepository.existsByName(tournamentCreateDto.getName())) {
            throw new EntityExistsException(String.format(TOURNAMENT_NAME_EXISTS_MSG, tournamentCreateDto.getName()));
        }

        Tournament tournament = tournamentMapper.toEntity(tournamentCreateDto);
        tournament.setStatus(TournamentStatus.CREATED);

        return tournamentMapper.toDto(tournamentRepository.save(tournament));
    }

    @Override
    public List<TournamentResponseDto> getAll() {
        return tournamentRepository.findAll()
                .stream()
                .map(tournamentMapper::toDto)
                .toList();
    }

    @Override
    public TournamentResponseDto getById(UUID id) {
        return tournamentRepository.findById(id)
                .map(tournamentMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(TOURNAMENT_NOT_FOUND_MSG + id));
    }

    @Override
    public TournamentResponseDto update(UUID id, TournamentUpdateDto tournamentUpdateDto) {
        return tournamentRepository.findById(id)
                .map(existingTournament -> {
                    tournamentMapper.updateEntityFromDto(tournamentUpdateDto, existingTournament);
                    return tournamentMapper.toDto(tournamentRepository.save(existingTournament));
                })
                .orElseThrow(() -> new EntityNotFoundException(TOURNAMENT_NOT_FOUND_MSG + id));
    }

    @Override
    public void delete(UUID id) {
        if (!tournamentRepository.existsById(id)) {
            throw new EntityNotFoundException(TOURNAMENT_NOT_FOUND_MSG + id);
        }
        tournamentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addPairToTournament(UUID tournamentId, UUID pairId) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        Pair pair = getPairOrThrow(pairId);

        if (tournament.getPairs().contains(pair)) {
            throw new IllegalArgumentException(PAIR_ALREADY_ADDED_MSG);
        }

        tournament.getPairs().add(pair);
        tournamentRepository.save(tournament);
    }

    @Override
    @Transactional
    public void removePairFromTournament(UUID tournamentId, UUID pairId) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        Pair pair = getPairOrThrow(pairId);

        if (!tournament.getPairs().contains(pair)) {
            throw new IllegalArgumentException(PAIR_NOT_IN_TOURNAMENT_MSG);
        }

        tournament.getPairs().remove(pair);
        tournamentRepository.save(tournament);
    }

    @Override
    public List<PairResponseDto> getPairsInTournament(UUID tournamentId) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        return pairMapper.toDtoList(tournament.getPairs());
    }

    @Override
    @Transactional
    public TournamentResponseDto startTournament(UUID tournamentId) {
        Tournament tournament = getTournamentOrThrow(tournamentId);

        if (tournament.getStatus() != TournamentStatus.CREATED) {
            throw new IllegalStateException(TOURNAMENT_START_VALIDATION_MSG);
        }

        TournamentStrategy strategy = tournamentStrategyFactory.getStrategy(tournament.getTournamentType());
        strategy.validateBeforeStart(tournament);

        tournament.setStatus(TournamentStatus.IN_PROGRESS);

        if (tournament.getMatches().isEmpty()) {
            List<Match> matches = strategy.generateMatches(tournament);
            tournament.getMatches().addAll(matches);
        }

        Tournament updated = tournamentRepository.save(tournament);
        return tournamentMapper.toDto(updated);
    }

    @Override
    @Transactional
    public TournamentResponseDto updateStatus(UUID id, TournamentStatusUpdateDto tournamentStatusUpdateDto) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TOURNAMENT_NOT_FOUND_MSG + id));

        TournamentStatus newStatus = getTournamentStatus(tournamentStatusUpdateDto, tournament);

        if (newStatus == TournamentStatus.IN_PROGRESS) {
            TournamentStrategy strategy = tournamentStrategyFactory.getStrategy(tournament.getTournamentType());
            strategy.validateBeforeStart(tournament);

            if (tournament.getMatches().isEmpty()) {
                List<Match> matches = strategy.generateMatches(tournament);
                tournament.getMatches().addAll(matches);
            }
        }

        tournament.setStatus(newStatus);

        // Solo establecer fecha de finalización automáticamente si no existe una fecha manual
        if (newStatus == TournamentStatus.FINISHED && tournament.getEndDate() == null) {
            tournament.setEndDate(LocalDateTime.now());
        }

        tournamentRepository.save(tournament);
        return tournamentMapper.toDto(tournament);
    }

    @Override
    public List<MatchResponseDto> getMatchesInTournament(UUID tournamentId) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        return matchMapper.toDtoList(tournament.getMatches());
    }

    @Override
    @Transactional
    public TournamentResponseDto tryFinalizeTournamentIfCompleted(UUID tournamentId) {
        Tournament tournament = getTournamentOrThrow(tournamentId);

        if (tournament.getStatus() == TournamentStatus.FINISHED) {
            return tournamentMapper.toDto(tournament);
        }

        boolean allMatchesCompleted = tournament.getMatches().stream()
                .allMatch(match -> match.getStatus() == MatchStatus.COMPLETED);

        if (!allMatchesCompleted) {
            return tournamentMapper.toDto(tournament);
        }

        tournament.setStatus(TournamentStatus.FINISHED);

        if (tournament.getEndDate() == null) {
            tournament.setEndDate(LocalDateTime.now());
        }

        return tournamentMapper.toDto(tournamentRepository.save(tournament));
    }

    @Override
    public List<PairStandingResponseDto> getStandings(UUID tournamentId) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        TournamentStrategy strategy = tournamentStrategyFactory.getStrategy(tournament.getTournamentType());

        List<PairStanding> standings = strategy.calculateStandings(tournament);

        return standings.stream()
                .map(pairStandingMapper::toDto)
                .toList();
    }

    @Override
    public TournamentProgressResponseDto getProgress(UUID tournamentId) {
        Tournament tournament = getTournamentOrThrow(tournamentId);

        int total = tournament.getMatches().size();
        long completed = tournament.getMatches().stream()
                .filter(match -> match.getStatus() == MatchStatus.COMPLETED)
                .count();

        double percentage = total == 0 ? 0 : (completed * 100.0) / total;

        return new TournamentProgressResponseDto(total, (int) completed, tournament.getStatus(), percentage);
    }

    @Override
    public List<TournamentSummaryResponseDto> getSummary() {
        return tournamentRepository.findAll().stream()
                .map(t -> new TournamentSummaryResponseDto(
                        t.getId(),
                        t.getName(),
                        t.getStatus(),
                        t.getPairs().size(),
                        t.getMatches().size()
                ))
                .toList();
    }

    @Override
    public TournamentStatus getStatus(UUID tournamentId) {
        return getTournamentOrThrow(tournamentId).getStatus();
    }

    @Override
    public PaginatedResponseDto<TournamentResponseDto> getAllPaginated(Pageable pageable) {
        Page<Tournament> tournamentsPage = tournamentRepository.findAll(pageable);

        List<TournamentResponseDto> tournaments = tournamentsPage.getContent()
                .stream()
                .map(tournamentMapper::toDto)
                .toList();

        return new PaginatedResponseDto<>(tournaments, tournamentsPage);
    }

    private TournamentStatus getTournamentStatus(TournamentStatusUpdateDto tournamentStatusUpdateDto, Tournament tournament) {
        TournamentStatus currentStatus = tournament.getStatus();
        TournamentStatus newStatus = tournamentStatusUpdateDto.getStatus();

        if (currentStatus == TournamentStatus.FINISHED) {
            throw new IllegalStateException(FINISHED_TOURNAMENT_UPDATE_MSG);
        }

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new IllegalArgumentException(String.format(INVALID_STATUS_TRANSITION_MSG, currentStatus, newStatus));
        }

        return newStatus;
    }

    private boolean isValidStatusTransition(TournamentStatus current, TournamentStatus next) {
        return switch (current) {
            case CREATED -> next == TournamentStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == TournamentStatus.FINISHED;
            default -> false;
        };
    }

    private Tournament getTournamentOrThrow(UUID tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException(TOURNAMENT_NOT_FOUND_MSG + tournamentId));
    }

    private Pair getPairOrThrow(UUID pairId) {
        return pairRepository.findById(pairId)
                .orElseThrow(() -> new EntityNotFoundException(PAIR_NOT_FOUND_MSG + pairId));
    }
}