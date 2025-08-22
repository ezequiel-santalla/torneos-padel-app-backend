package com.eze_dev.torneos.dto.response;

import com.eze_dev.torneos.types.MatchStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MatchResponseDto(
        UUID id,
        UUID tournamentId,
        PairResponseDto pair1,
        PairResponseDto pair2,
        Integer pair1Score,
        Integer pair2Score,
        LocalDateTime scheduledDate,
        MatchStatus status
) {}
