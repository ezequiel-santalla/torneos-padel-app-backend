package com.eze_dev.torneos.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCreateDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Permission IDs are required")
    private Set<Long> permissionIds;
}
