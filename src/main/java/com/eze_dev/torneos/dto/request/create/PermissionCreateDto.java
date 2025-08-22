package com.eze_dev.torneos.dto.request.create;

import jakarta.validation.constraints.NotBlank;

public record PermissionCreateDto(
        @NotBlank(message = "Name is required") String name
) {}
