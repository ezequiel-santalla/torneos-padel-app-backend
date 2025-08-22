package com.eze_dev.torneos.service.interfaces;

import com.eze_dev.torneos.dto.request.create.TournamentCreateDto;
import com.eze_dev.torneos.dto.response.*;
import com.eze_dev.torneos.dto.request.update.TournamentStatusUpdateDto;
import com.eze_dev.torneos.dto.request.update.TournamentUpdateDto;
import com.eze_dev.torneos.model.Match;
import com.eze_dev.torneos.types.TournamentStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITournamentService {

    TournamentResponseDto create(TournamentCreateDto tournamentCreateDto);
    List<TournamentResponseDto> getAll();
    TournamentResponseDto getById(UUID id);
    TournamentResponseDto update(UUID id, TournamentUpdateDto tournamentUpdateDto);
    void delete(UUID id);

    void addPairToTournament(UUID tournamentId, UUID pairId);
    void removePairFromTournament(UUID tournamentId, UUID pairId);
    List<PairResponseDto> getPairsInTournament(UUID tournamentId);
    TournamentResponseDto startTournament(UUID tournamentId);

    TournamentResponseDto updateStatus(UUID id, TournamentStatusUpdateDto tournamentStatusUpdateDto);

    List<MatchResponseDto> getMatchesInTournament(UUID tournamentId);

    void tryFinalizeTournamentIfCompleted(UUID tournamentId);
    List<PairStandingResponseDto> getStandings(UUID tournamentId);

    TournamentProgressResponseDto getProgress(UUID tournamentId);

    List<TournamentSummaryResponseDto> getSummary();
    TournamentStatus getStatus(UUID tournamentId);

    PaginatedResponseDto<TournamentResponseDto> getAllPaginated(Pageable pageable);

    List<Match> processMatchCompletionAndAdvance(UUID tournamentId);
}