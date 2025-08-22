package com.eze_dev.torneos.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PairCreateDto(
        @NotNull UUID player1Id,
        @NotNull UUID player2Id,
        @NotBlank String teamName
) {}
