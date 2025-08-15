package com.eze_dev.torneos.controller;

import com.eze_dev.torneos.service.interfaces.IEnumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/enums")
@RequiredArgsConstructor
public class EnumController {

    private final IEnumService enumService;

    @GetMapping("/category-types")
    public ResponseEntity<List<String>> getCategoryTypes() {
        return ResponseEntity.ok(enumService.getCategoryTypes());
    }

    @GetMapping("/gender-types")
    public ResponseEntity<List<String>> getGenderTypes() {
        return ResponseEntity.ok(enumService.getGenderTypes());
    }

    @GetMapping("/match-statuses")
    public ResponseEntity<List<String>> getMatchStatuses() {
        return ResponseEntity.ok(enumService.getMatchStatuses());
    }

    @GetMapping("/tournament-statuses")
    public ResponseEntity<List<String>> getTournamentStatuses() {
        return ResponseEntity.ok(enumService.getTournamentStatuses());
    }

    @GetMapping("/tournament-types")
    public ResponseEntity<List<String>> getTournamentTypes() {
        return ResponseEntity.ok(enumService.getTournamentTypes());
    }

    @GetMapping("/winning-match-rule-types")
    public ResponseEntity<List<String>> getWinningMatchRuleTypes() {
        return ResponseEntity.ok(enumService.getWinningMatchRuleTypes());
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, List<String>>> getAllEnums() {
        return ResponseEntity.ok(enumService.getAllEnums());
    }

    @GetMapping("/category-types/info")
    public ResponseEntity<List<Map<String, String>>> getCategoryTypesWithInfo() {
        return ResponseEntity.ok(enumService.getCategoryTypesWithInfo());
    }

    @GetMapping("/gender-types/info")
    public ResponseEntity<List<Map<String, String>>> getGenderTypesWithInfo() {
        return ResponseEntity.ok(enumService.getGenderTypesWithInfo());
    }

    @GetMapping("/match-statuses/info")
    public ResponseEntity<List<Map<String, String>>> getMatchStatusesWithInfo() {
        return ResponseEntity.ok(enumService.getMatchStatusesWithInfo());
    }

    @GetMapping("/tournament-statuses/info")
    public ResponseEntity<List<Map<String, String>>> getTournamentStatusesWithInfo() {
        return ResponseEntity.ok(enumService.getTournamentStatusesWithInfo());
    }

    @GetMapping("/tournament-types/info")
    public ResponseEntity<List<Map<String, String>>> getTournamentTypesWithInfo() {
        return ResponseEntity.ok(enumService.getTournamentTypesWithInfo());
    }

    @GetMapping("/winning-match-rule-types/info")
    public ResponseEntity<List<Map<String, String>>> getWinningMatchRuleTypesWithInfo() {
        return ResponseEntity.ok(enumService.getWinningMatchRuleTypesWithInfo());
    }
}
