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
public class PlayerSummaryResponseDto {

    private UUID id;
    private String name;
    private String lastName;
}

