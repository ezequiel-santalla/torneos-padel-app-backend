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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final ITournamentService tournamentService;

    // ===== ADMIN ENDPOINTS =====

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentResponseDto> createTournament(@Valid @RequestBody TournamentCreateDto tournamentCreateDto) {
        return ResponseEntity.ok(tournamentService.create(tournamentCreateDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentResponseDto> updateTournament(@PathVariable UUID id, @Valid @RequestBody TournamentUpdateDto tournamentUpdateDto) {
        return ResponseEntity.ok(tournamentService.update(id, tournamentUpdateDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTournament(@PathVariable UUID id) {
        tournamentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/pairs/{pairId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addPairToTournament(@PathVariable UUID id, @PathVariable UUID pairId) {
        tournamentService.addPairToTournament(id, pairId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/pairs/{pairId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removePairFromTournament(@PathVariable UUID id, @PathVariable UUID pairId) {
        tournamentService.removePairFromTournament(id, pairId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentResponseDto> startTournament(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.startTournament(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentResponseDto> updateTournamentStatus(@PathVariable UUID id, @Valid @RequestBody TournamentStatusUpdateDto tournamentStatusUpdateDto) {
        return ResponseEntity.ok(tournamentService.updateStatus(id, tournamentStatusUpdateDto));
    }

    // ===== AUTHENTICATED USERS ENDPOINTS =====

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<TournamentResponseDto>> getAllTournamentsPaginated(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(tournamentService.getAllPaginated(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponseDto> getTournamentById(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.getById(id));
    }

    @GetMapping("/{id}/pairs")
    public ResponseEntity<List<PairResponseDto>> getPairsInTournament(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.getPairsInTournament(id));
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
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TournamentStatus> getTournamentStatusOnly(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.getStatus(id));
    }

    // ===== AUTHENTICATED USERS ENDPOINTS =====

//    @GetMapping("/my-tournaments")
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
//    public ResponseEntity<List<TournamentResponseDto>> getMyTournaments(Authentication authentication) {
//        return ResponseEntity.ok(tournamentService.getTournamentsByUser(authentication.getName()));
//    }
//
//    @GetMapping("/available")
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
//    public ResponseEntity<List<TournamentResponseDto>> getAvailableTournaments() {
//        return ResponseEntity.ok(tournamentService.getAvailableTournaments());
//    }
}
