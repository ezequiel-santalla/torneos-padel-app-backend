package com.eze_dev.torneos.dto.response;

import java.util.UUID;

public record PairResponseDto(
        UUID id,
        String teamName,
        PlayerSummaryResponseDto player1,
        PlayerSummaryResponseDto player2
) {}
