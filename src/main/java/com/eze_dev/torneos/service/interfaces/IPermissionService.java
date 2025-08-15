package com.eze_dev.torneos.service.interfaces;

import com.eze_dev.torneos.dto.request.create.PermissionCreateDto;
import com.eze_dev.torneos.dto.request.update.PermissionUpdateDto;
import com.eze_dev.torneos.dto.response.PermissionResponseDto;

import java.util.List;

public interface IPermissionService {

    PermissionResponseDto create(PermissionCreateDto permission);
    List<PermissionResponseDto> getAll();
    PermissionResponseDto getById(Long id);
    PermissionResponseDto update(Long id, PermissionUpdateDto permission);
    void delete(Long id);
}
