package com.eze_dev.torneos.controller;

import com.eze_dev.torneos.dto.request.update.PlayerUpdateDto;
import com.eze_dev.torneos.dto.response.PaginatedResponseDto;
import com.eze_dev.torneos.dto.response.PlayerResponseDto;
import com.eze_dev.torneos.dto.response.PlayerSummaryResponseDto;
import com.eze_dev.torneos.service.interfaces.IPlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
public class PlayerController {

    private final IPlayerService playerService;

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<PlayerSummaryResponseDto>> getAllSummarizedPlayersPaginated(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(playerService.getAllSummarizedPaginated(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlayerResponseDto> getPlayerById(@PathVariable UUID id) {
        return ResponseEntity.ok(playerService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlayerResponseDto> updatePlayer(@PathVariable UUID id, @Valid @RequestBody PlayerUpdateDto playerUpdateDto) {
        return ResponseEntity.ok(playerService.update(id, playerUpdateDto));
    }
}
