package com.eze_dev.torneos.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RoleCreateDto(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Permission IDs are required") Set<Long> permissionIds
) {}
