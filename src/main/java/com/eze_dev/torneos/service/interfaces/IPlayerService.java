package com.eze_dev.torneos.service.interfaces;

import com.eze_dev.torneos.dto.request.update.PlayerUpdateDto;
import com.eze_dev.torneos.dto.request.update.UserUpdateDto;
import com.eze_dev.torneos.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IPlayerService {

    List<PlayerResponseDto> getAll();
    PlayerResponseDto getById(UUID id);
    PlayerResponseDto update(UUID id, PlayerUpdateDto playerUpdateDto);

    PaginatedResponseDto<PlayerResponseDto> getAllPaginated(Pageable pageable);
    PaginatedResponseDto<PlayerSummaryResponseDto> getAllSummarizedPaginated(Pageable pageable);
}
