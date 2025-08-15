package com.eze_dev.torneos.service.interfaces;

import com.eze_dev.torneos.dto.response.MatchResponseDto;
import com.eze_dev.torneos.dto.request.update.MatchResultUpdateDto;

import java.util.UUID;

public interface IMatchService {

    MatchResponseDto updateMatchResult(UUID tournamentId, UUID id, MatchResultUpdateDto matchResultUpdateDto);
}
