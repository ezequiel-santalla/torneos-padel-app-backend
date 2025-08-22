package com.eze_dev.torneos.dto.request.update;

import com.eze_dev.torneos.types.TournamentStatus;

public record TournamentStatusUpdateDto(
        TournamentStatus status
) {}
