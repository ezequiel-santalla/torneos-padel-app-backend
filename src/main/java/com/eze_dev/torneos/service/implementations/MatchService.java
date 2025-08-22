package com.eze_dev.torneos.service.implementations;

import com.eze_dev.torneos.dto.response.MatchResponseDto;
import com.eze_dev.torneos.dto.request.update.MatchResultUpdateDto;
import com.eze_dev.torneos.mapper.MatchMapper;
import com.eze_dev.torneos.model.Match;
import com.eze_dev.torneos.repository.MatchRepository;
import com.eze_dev.torneos.service.interfaces.IMatchService;
import com.eze_dev.torneos.service.interfaces.ITournamentService;
import com.eze_dev.torneos.types.MatchStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService implements IMatchService {

    private static final String MATCH_NOT_FOUND_MSG = "Match not found with ID: ";

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final ITournamentService tournamentService;

    @Override
    @Transactional
    public MatchResponseDto updateMatchResult(UUID tournamentId, UUID matchId, MatchResultUpdateDto dto) {
        Match match = getMatchOrThrow(matchId);

        if (!match.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("Match does not belong to the specified tournament");
        }

        if (match.getStatus() == MatchStatus.COMPLETED) {
            throw new IllegalStateException("Match is already completed");
        }

        match.setPair1Score(dto.pair1Score());
        match.setPair2Score(dto.pair2Score());
        match.setStatus(MatchStatus.COMPLETED);

        Match savedMatch = matchRepository.save(match);

        try {
            List<Match> newMatches = tournamentService.processMatchCompletionAndAdvance(tournamentId);
            if (!newMatches.isEmpty()) {
                log.info("Generated {} new matches for tournament {} after completing match {}",
                        newMatches.size(), tournamentId, matchId);
            }
        } catch (Exception e) {
            log.error("Failed to process match completion and advance for tournament {}: {}",
                    tournamentId, e.getMessage(), e);
            throw e;
        }

        try {
            tournamentService.tryFinalizeTournamentIfCompleted(tournamentId);
        } catch (Exception e) {
            log.warn("Failed to finalize tournament {} automatically: {}", tournamentId, e.getMessage());
        }

        return matchMapper.toDto(savedMatch);
    }

    private Match getMatchOrThrow(UUID matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException(MATCH_NOT_FOUND_MSG + matchId));
    }
}