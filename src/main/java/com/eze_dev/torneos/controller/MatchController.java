package com.eze_dev.torneos.controller;

import com.eze_dev.torneos.dto.response.MatchResponseDto;
import com.eze_dev.torneos.dto.request.update.MatchResultUpdateDto;
import com.eze_dev.torneos.service.interfaces.IMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tournaments/{tournamentId}/matches")
@RequiredArgsConstructor
public class MatchController {

    private final IMatchService matchService;

    @PutMapping("/{id}/result")
    public ResponseEntity<MatchResponseDto> updateMatchResult(@PathVariable UUID tournamentId, @PathVariable UUID id, @Valid @RequestBody MatchResultUpdateDto matchResultUpdateDto) {
        return ResponseEntity.ok(matchService.updateMatchResult(tournamentId, id, matchResultUpdateDto));
    }
}
