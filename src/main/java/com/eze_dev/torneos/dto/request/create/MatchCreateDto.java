package com.eze_dev.torneos.dto.request.create;

import com.eze_dev.torneos.types.MatchStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record MatchCreateDto(
        @NotNull UUID tournamentId,
        @NotNull UUID pair1Id,
        @NotNull UUID pair2Id,
        @NotNull LocalDateTime scheduledDate,
        @NotNull MatchStatus status
) {}
