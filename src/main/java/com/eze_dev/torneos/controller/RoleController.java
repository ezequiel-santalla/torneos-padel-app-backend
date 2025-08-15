package com.eze_dev.torneos.controller;

import com.eze_dev.torneos.dto.request.create.RoleCreateDto;
import com.eze_dev.torneos.dto.request.update.PermissionUpdateDto;
import com.eze_dev.torneos.dto.request.update.RoleUpdateDto;
import com.eze_dev.torneos.dto.response.PermissionResponseDto;
import com.eze_dev.torneos.dto.response.RoleResponseDto;
import com.eze_dev.torneos.service.interfaces.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleCreateDto roleCreateDto) {
        return ResponseEntity.ok(roleService.create(roleCreateDto));
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateDto roleUpdateDto) {
        return ResponseEntity.ok(roleService.update(id, roleUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
