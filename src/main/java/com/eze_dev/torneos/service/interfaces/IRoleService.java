package com.eze_dev.torneos.service.interfaces;

import com.eze_dev.torneos.dto.request.create.RoleCreateDto;
import com.eze_dev.torneos.dto.request.update.RoleUpdateDto;
import com.eze_dev.torneos.dto.response.RoleResponseDto;

import java.util.List;

public interface IRoleService {

    RoleResponseDto create(RoleCreateDto role);
    List<RoleResponseDto> getAll();
    RoleResponseDto getById(Long id);
    RoleResponseDto update(Long id, RoleUpdateDto role);
    void delete(Long id);
}
