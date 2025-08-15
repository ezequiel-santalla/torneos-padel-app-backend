package com.eze_dev.torneos.service.implementations;

import com.eze_dev.torneos.dto.request.create.PairCreateDto;
import com.eze_dev.torneos.dto.response.PairResponseDto;
import com.eze_dev.torneos.dto.request.update.PairUpdateDto;
import com.eze_dev.torneos.mapper.PairMapper;
import com.eze_dev.torneos.model.Pair;
import com.eze_dev.torneos.model.Player;
import com.eze_dev.torneos.repository.PairRepository;
import com.eze_dev.torneos.repository.PlayerRepository;
import com.eze_dev.torneos.service.interfaces.IPairService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PairService implements IPairService {

    private final PairRepository pairRepository;
    private final PlayerRepository playerRepository;
    private final PairMapper pairMapper;

    @Override
    public PairResponseDto create(PairCreateDto pairCreateDto) {
        if (pairRepository.findByTeamName(pairCreateDto.getTeamName()).isPresent()) {
            throw new EntityExistsException("Team name already exists.");
        }

        validateDifferentPlayers(pairCreateDto.getPlayer1Id(), pairCreateDto.getPlayer2Id());
        validatePairDoesNotExist(pairCreateDto.getPlayer1Id(), pairCreateDto.getPlayer2Id());

        Player player1 = getPlayerOrThrow(pairCreateDto.getPlayer1Id(), "Player 1 not found");
        Player player2 = getPlayerOrThrow(pairCreateDto.getPlayer2Id(), "Player 2 not found");

        Pair pair = Pair.builder()
                .teamName(pairCreateDto.getTeamName())
                .player1(player1)
                .player2(player2)
                .build();

        return pairMapper.toDto(pairRepository.save(pair));
    }

    @Override
    public List<PairResponseDto> getAll() {
        return pairRepository.findAll()
                .stream()
                .map(pairMapper::toDto)
                .toList();
    }

    @Override
    public PairResponseDto getById(UUID id) {
        return pairRepository.findById(id)
                .map(pairMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Pair not found with ID: " + id));
    }

    @Override
    @Transactional
    public PairResponseDto update(UUID id, PairUpdateDto pairUpdateDto) {
        Pair pair = pairRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pair not found with ID: " + id));

        validateDifferentPlayers(pairUpdateDto.getPlayer1Id(), pairUpdateDto.getPlayer2Id());

        Player player1 = getPlayerOrThrow(pairUpdateDto.getPlayer1Id(), "Player 1 not found");
        Player player2 = getPlayerOrThrow(pairUpdateDto.getPlayer2Id(), "Player 2 not found");

        pair.setPlayer1(player1);
        pair.setPlayer2(player2);
        pair.setTeamName(pairUpdateDto.getTeamName());

        return pairMapper.toDto(pairRepository.save(pair));
    }

    @Override
    public void delete(UUID id) {
        if (!pairRepository.existsById(id)) {
            throw new EntityNotFoundException("Pair not found with ID: " + id);
        }
        pairRepository.deleteById(id);
    }

    @Override
    public List<PairResponseDto> getPairsByPlayerId(UUID playerId) {
        return pairRepository.findByPlayer1_IdOrPlayer2_Id(playerId, playerId)
                .stream()
                .map(pairMapper::toDto)
                .toList();
    }

    @Override
    public boolean existsByTeamName(String teamName) {
        return pairRepository.findByTeamName(teamName).isPresent();
    }

    private void validateDifferentPlayers(UUID player1Id, UUID player2Id) {
        if (player1Id.equals(player2Id)) {
            throw new IllegalArgumentException("A pair must consist of two different players.");
        }
    }

    private void validatePairDoesNotExist(UUID player1Id, UUID player2Id) {
        List<Pair> existingPairs = pairRepository.findByPlayer1_IdOrPlayer2_Id(player1Id, player2Id);

        boolean pairExists = existingPairs.stream().anyMatch(pair ->
                (pair.getPlayer1().getId().equals(player1Id) && pair.getPlayer2().getId().equals(player2Id)) ||
                        (pair.getPlayer1().getId().equals(player2Id) && pair.getPlayer2().getId().equals(player1Id))
        );

        if (pairExists) {
            throw new EntityExistsException("A pair with these players already exists.");
        }
    }

    private Player getPlayerOrThrow(UUID playerId, String errorMessage) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));
    }
}
