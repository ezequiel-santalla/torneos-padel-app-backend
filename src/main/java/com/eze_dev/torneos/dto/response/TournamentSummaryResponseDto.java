package com.eze_dev.torneos.dto.response;

import com.eze_dev.torneos.types.TournamentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TournamentSummaryResponseDto {

    private UUID id;
    private String name;
    private TournamentStatus status;
    private int pairCount;
    private int matchCount;
}
