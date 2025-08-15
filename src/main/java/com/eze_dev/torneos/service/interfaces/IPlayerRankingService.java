package com.eze_dev.torneos.service.interfaces;

import com.eze_dev.torneos.dto.response.PaginatedResponseDto;
import com.eze_dev.torneos.dto.response.PlayerRankingResponseDto;
import com.eze_dev.torneos.types.CategoryType;
import com.eze_dev.torneos.types.GenderType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPlayerRankingService {

    List<PlayerRankingResponseDto> getPlayerRankings(CategoryType category, GenderType gender);
    PaginatedResponseDto<PlayerRankingResponseDto> getPlayerRankingsPaginated(CategoryType category, GenderType gender, Pageable pageable);
}
