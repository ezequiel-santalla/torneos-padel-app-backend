package com.eze_dev.torneos.controller;

import com.eze_dev.torneos.dto.request.create.PermissionCreateDto;
import com.eze_dev.torneos.dto.request.update.PermissionUpdateDto;
import com.eze_dev.torneos.dto.response.PermissionResponseDto;
import com.eze_dev.torneos.service.interfaces.IPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final IPermissionService permissionService;

    @PostMapping
    public ResponseEntity<PermissionResponseDto> createPermission(@Valid @RequestBody PermissionCreateDto permissionCreateDto) {
        return ResponseEntity.ok(permissionService.create(permissionCreateDto));
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponseDto>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponseDto> getPermissionById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponseDto> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionUpdateDto permissionUpdateDto) {
        return ResponseEntity.ok(permissionService.update(id, permissionUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        permissionService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
