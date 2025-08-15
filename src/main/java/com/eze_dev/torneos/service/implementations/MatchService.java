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

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService implements IMatchService {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final ITournamentService tournamentService;

    @Override
    @Transactional
    public MatchResponseDto updateMatchResult(UUID tournamentId, UUID matchId, MatchResultUpdateDto dto) {
        Match match = getMatch(matchId);

        match.setPair1Score(dto.getPair1Score());
        match.setPair2Score(dto.getPair2Score());
        match.setStatus(MatchStatus.COMPLETED);

        matchRepository.save(match);
        log.info("Match {} updated and marked as COMPLETED", matchId);

        try {
            tournamentService.tryFinalizeTournamentIfCompleted(match.getTournament().getId());
            log.info("Tournament {} finalized automatically after match update", match.getTournament().getId());
        } catch (Exception e) {
            log.warn("Failed to finalize tournament {} automatically: {}", match.getTournament().getId(), e.getMessage());
        }
        return matchMapper.toDto(match);
    }

    private Match getMatch(UUID matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found with ID: " + matchId));
    }
}

