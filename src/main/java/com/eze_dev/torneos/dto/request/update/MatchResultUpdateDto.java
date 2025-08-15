package com.eze_dev.torneos.dto.request.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResultUpdateDto {

    private Integer pair1Score;
    private Integer pair2Score;
}
