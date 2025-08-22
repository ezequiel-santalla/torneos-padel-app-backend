package com.eze_dev.torneos.dto.request.update;

import java.util.Set;

public record RoleUpdateDto(
        String name,
        Set<Long> permissionIds
) {}
