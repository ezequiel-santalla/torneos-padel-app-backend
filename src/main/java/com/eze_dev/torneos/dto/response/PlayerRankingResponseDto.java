package com.eze_dev.torneos.dto.response;

import com.eze_dev.torneos.types.GenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerRankingResponseDto {

    private UUID id;
    private String name;
    private String lastName;
    private GenderType genderType;
    private int totalPoints;
    private int tournamentsPlayed;
}
