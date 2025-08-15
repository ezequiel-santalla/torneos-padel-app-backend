package com.eze_dev.torneos.dto.request.create;

import com.eze_dev.torneos.types.MatchStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchCreateDto {

    @NotNull
    private UUID tournamentId;

    @NotNull
    private UUID pair1Id;

    @NotNull
    private UUID pair2Id;

    @NotNull
    private LocalDateTime scheduledDate;

    @NotNull
    private MatchStatus status;
}
