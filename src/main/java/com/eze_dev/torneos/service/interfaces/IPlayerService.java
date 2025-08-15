package com.eze_dev.torneos.service.interfaces;

import com.eze_dev.torneos.dto.response.PaginatedResponseDto;
import com.eze_dev.torneos.dto.response.PlayerResponseDto;
import com.eze_dev.torneos.dto.response.TournamentResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IPlayerService {

    List<PlayerResponseDto> getAll();
    PlayerResponseDto getById(UUID id);

    PaginatedResponseDto<PlayerResponseDto> getAllPaginated(Pageable pageable);
}
