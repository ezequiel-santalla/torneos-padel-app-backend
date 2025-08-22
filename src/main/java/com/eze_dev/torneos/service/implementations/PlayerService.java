package com.eze_dev.torneos.service.implementations;

import com.eze_dev.torneos.dto.request.update.PlayerUpdateDto;
import com.eze_dev.torneos.dto.response.PaginatedResponseDto;
import com.eze_dev.torneos.dto.response.PlayerResponseDto;
import com.eze_dev.torneos.mapper.PlayerMapper;
import com.eze_dev.torneos.model.Player;
import com.eze_dev.torneos.repository.PlayerRepository;
import com.eze_dev.torneos.service.interfaces.IPlayerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService implements IPlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    @Override
    public List<PlayerResponseDto> getAll() {
        return playerRepository.findAll()
                .stream()
                .map(playerMapper::toDto)
                .toList();
    }

    @Override
    public PlayerResponseDto getById(UUID id) {
        return playerRepository.findById(id)
                .map(playerMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + id));
    }

    @Override
    public PlayerResponseDto update(UUID id, PlayerUpdateDto playerUpdateDto) {
        return playerRepository.findById(id)
                .map(existingPlayer -> {
                    playerMapper.updateEntityFromDto(playerUpdateDto, existingPlayer);

                    return playerMapper.toDto(playerRepository.save(existingPlayer));
                })
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + id));
    }

    @Override
    public PaginatedResponseDto<PlayerResponseDto> getAllPaginated(Pageable pageable) {
        Page<Player> playersPage = playerRepository.findAll(pageable);

        List<PlayerResponseDto> players = playersPage.getContent()
                .stream()
                .map(playerMapper::toDto)
                .toList();

        return new PaginatedResponseDto<>(players, playersPage);
    }
}
