package com.eze_dev.torneos.controller;

import com.eze_dev.torneos.dto.response.PaginatedResponseDto;
import com.eze_dev.torneos.dto.response.PlayerResponseDto;
import com.eze_dev.torneos.service.interfaces.IPlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
public class PlayerController {

    private final IPlayerService playerService;

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<PlayerResponseDto>> getAllPlayersPaginated(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(playerService.getAllPaginated(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponseDto> getPlayerById(@PathVariable UUID id) {
        return ResponseEntity.ok(playerService.getById(id));
    }
}
