package com.eze_dev.torneos.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PairCreateDto {

    @NotNull
    private UUID player1Id;

    @NotNull
    private UUID player2Id;

    @NotBlank
    private String teamName;
}

