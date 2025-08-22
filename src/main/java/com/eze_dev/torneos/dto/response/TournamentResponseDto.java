package com.eze_dev.torneos.dto.response;

import com.eze_dev.torneos.types.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record TournamentResponseDto(
        UUID id,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String address,
        TournamentType tournamentType,
        CategoryType categoryType,
        GenderType genderType,
        TournamentStatus status,
        WinningMatchRuleType winningMatchRule
) {}
