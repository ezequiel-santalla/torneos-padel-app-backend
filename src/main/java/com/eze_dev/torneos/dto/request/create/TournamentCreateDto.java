package com.eze_dev.torneos.dto.request.create;

import com.eze_dev.torneos.types.CategoryType;
import com.eze_dev.torneos.types.GenderType;
import com.eze_dev.torneos.types.TournamentType;
import com.eze_dev.torneos.types.WinningMatchRuleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentCreateDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Winning match rule is required")
    private WinningMatchRuleType winningMatchRule;

    @NotNull(message = "Tournament type is required")
    private TournamentType tournamentType;

    @NotNull(message = "Category is required")
    private CategoryType categoryType;

    @NotNull(message = "Gender is required")
    private GenderType genderType;
}

