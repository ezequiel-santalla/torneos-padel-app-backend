package com.eze_dev.torneos.dto.request.update;

import java.util.UUID;

public record PairUpdateDto(
        UUID player1Id,
        UUID player2Id,
        String teamName
) {}
