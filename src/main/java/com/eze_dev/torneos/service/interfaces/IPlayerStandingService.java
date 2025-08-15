package com.eze_dev.torneos.service.interfaces;

import com.eze_dev.torneos.dto.response.PaginatedResponseDto;
import com.eze_dev.torneos.dto.response.PlayerRankingResponseDto;
import com.eze_dev.torneos.dto.response.PlayerStandingResponseDto;
import com.eze_dev.torneos.types.CategoryType;
import com.eze_dev.torneos.types.GenderType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IPlayerStandingService {

    PlayerStandingResponseDto getPlayerStandingById(UUID id);
    List<PlayerStandingResponseDto> getAllPlayersStandings();
}
