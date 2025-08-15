package com.eze_dev.torneos.dto.response;

import com.eze_dev.torneos.types.MatchStatus;
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
public class MatchResponseDto {

    private UUID id;
    private UUID tournamentId;
    private PairResponseDto pair1;
    private PairResponseDto pair2;
    private Integer pair1Score;
    private Integer pair2Score;
    private LocalDateTime scheduledDate;
    private MatchStatus status;
}
