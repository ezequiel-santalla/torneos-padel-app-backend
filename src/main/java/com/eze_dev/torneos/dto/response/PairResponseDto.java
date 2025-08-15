package com.eze_dev.torneos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PairResponseDto {

    private UUID id;
    private String teamName;
    private PlayerSummaryResponseDto player1;
    private PlayerSummaryResponseDto player2;
}

