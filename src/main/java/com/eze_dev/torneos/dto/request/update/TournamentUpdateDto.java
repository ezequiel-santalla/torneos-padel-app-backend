package com.eze_dev.torneos.dto.request.update;

import com.eze_dev.torneos.types.CategoryType;
import com.eze_dev.torneos.types.GenderType;
import com.eze_dev.torneos.types.TournamentType;
import com.eze_dev.torneos.types.WinningMatchRuleType;

import java.time.LocalDateTime;

public record TournamentUpdateDto(
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String address,
        TournamentType tournamentType,
        GenderType genderType,
        CategoryType categoryType,
        WinningMatchRuleType winningMatchRule
) {}
