package com.eze_dev.torneos.dto.request.create;

import com.eze_dev.torneos.types.CategoryType;
import com.eze_dev.torneos.types.GenderType;
import com.eze_dev.torneos.types.TournamentType;
import com.eze_dev.torneos.types.WinningMatchRuleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TournamentCreateDto(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Start date is required") LocalDateTime startDate,
        @NotNull(message = "End date is required") LocalDateTime endDate,
        @NotBlank(message = "Address is required") String address,
        @NotNull(message = "Winning match rule is required") WinningMatchRuleType winningMatchRule,
        @NotNull(message = "Tournament type is required") TournamentType tournamentType,
        @NotNull(message = "Category is required") CategoryType categoryType,
        @NotNull(message = "Gender is required") GenderType genderType
) {}
