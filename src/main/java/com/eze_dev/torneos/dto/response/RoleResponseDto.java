package com.eze_dev.torneos.dto.response;

import java.util.Set;

public record RoleResponseDto(
        Long id,
        String name,
        Set<PermissionResponseDto> permissions) {}
