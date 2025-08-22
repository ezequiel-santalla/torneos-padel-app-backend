package com.eze_dev.torneos.controller;

import com.eze_dev.torneos.dto.response.PlayerRankingResponseDto;
import com.eze_dev.torneos.service.interfaces.IPlayerRankingService;
import com.eze_dev.torneos.types.CategoryType;
import com.eze_dev.torneos.types.GenderType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/player-rankings")
@RequiredArgsConstructor
public class PlayerRankingController {

    private final IPlayerRankingService playerRankingService;

    @GetMapping
    public ResponseEntity<List<PlayerRankingResponseDto>> getPlayerRankings(
            @RequestParam(required = false) CategoryType category,
            @RequestParam(required = false) GenderType gender) {

        return ResponseEntity.ok(playerRankingService.getPlayerRankings(category, gender));
    }
}
