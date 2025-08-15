package com.eze_dev.torneos.controller;

import com.eze_dev.torneos.dto.request.create.TournamentCreateDto;
import com.eze_dev.torneos.dto.response.*;
import com.eze_dev.torneos.dto.request.update.TournamentStatusUpdateDto;
import com.eze_dev.torneos.dto.request.update.TournamentUpdateDto;
import com.eze_dev.torneos.service.interfaces.ITournamentService;
import com.eze_dev.torneos.types.TournamentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final ITournamentService tournamentService;

    @PostMapping
    public ResponseEntity<TournamentResponseDto> createTournament(@Valid @RequestBody TournamentCreateDto tournamentCreateDto) {
        return ResponseEntity.ok(tournamentService.create(tournamentCreateDto));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<TournamentResponseDto>> getAllTournamentsPaginated(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(tournamentService.getAllPaginated(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponseDto> getTournamentById(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponseDto> updateTournament(@PathVariable UUID id, @Valid @RequestBody TournamentUpdateDto tournamentUpdateDto) {
        return ResponseEntity.ok(tournamentService.update(id, tournamentUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable UUID id) {
        tournamentService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/pairs/{pairId}")
    public ResponseEntity<Void> addPairToTournament(@PathVariable UUID id, @PathVariable UUID pairId) {
        tournamentService.addPairToTournament(id, pairId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/pairs/{pairId}")
    public ResponseEntity<Void> removePairFromTournament(@PathVariable UUID id, @PathVariable UUID pairId) {
        tournamentService.removePairFromTournament(id, pairId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TournamentResponseDto> startTournament(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.startTournament(id));
    }

    @GetMapping("/{id}/pairs")
    public ResponseEntity<List<PairResponseDto>> getPairsInTournament(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.getPairsInTournament(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TournamentResponseDto> updateTournamentStatus(@PathVariable UUID id, @Valid @RequestBody TournamentStatusUpdateDto tournamentStatusUpdateDto) {
        return ResponseEntity.ok(tournamentService.updateStatus(id, tournamentStatusUpdateDto));
    }

    @GetMapping("/{id}/matches")
    public ResponseEntity<List<MatchResponseDto>> getMatchesByTournament(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.getMatchesInTournament(id));
    }

    @GetMapping("/{id}/standings")
    public ResponseEntity<List<PairStandingResponseDto>> getStandingsByTournament(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.getStandings(id));
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<TournamentProgressResponseDto> getTournamentProgress(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.getProgress(id));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<TournamentSummaryResponseDto>> getTournamentSummaries() {
        return ResponseEntity.ok(tournamentService.getSummary());
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<TournamentStatus> getTournamentStatusOnly(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.getStatus(id));
    }

}
