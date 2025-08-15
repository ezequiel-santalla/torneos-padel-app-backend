package com.eze_dev.torneos.dto.response;

import com.eze_dev.torneos.types.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentProgressResponseDto {

    private int totalMatches;
    private int completedMatches;
    private TournamentStatus status;
    private double completionPercentage;
}
