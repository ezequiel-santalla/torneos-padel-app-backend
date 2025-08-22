package com.eze_dev.torneos.dto.response;

import com.eze_dev.torneos.types.GenderType;

import java.util.UUID;

public record PlayerRankingResponseDto(
        UUID id,
        String name,
        String lastName,
        GenderType genderType,
        int position,
        int totalPoints,
        int tournamentsPlayed
) {}
