package com.eze_dev.torneos.controller;

import com.eze_dev.torneos.dto.response.PlayerStandingResponseDto;
import com.eze_dev.torneos.service.interfaces.IPlayerStandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/player-standings")
@RequiredArgsConstructor
public class PlayerStandingController {

    private final IPlayerStandingService playerStandingService;

    @GetMapping
    public ResponseEntity<List<PlayerStandingResponseDto>> getAllPlayersStandings() {
        return ResponseEntity.ok(playerStandingService.getAllPlayersStandings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerStandingResponseDto> getPlayerStandingById(@PathVariable UUID id) {
        return ResponseEntity.ok(playerStandingService.getPlayerStandingById(id));
    }
}
