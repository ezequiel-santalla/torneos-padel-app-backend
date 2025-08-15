package com.eze_dev.torneos.service.implementations;

import com.eze_dev.torneos.dto.request.create.PermissionCreateDto;
import com.eze_dev.torneos.dto.request.update.PermissionUpdateDto;
import com.eze_dev.torneos.dto.response.PermissionResponseDto;
import com.eze_dev.torneos.mapper.PermissionMapper;
import com.eze_dev.torneos.repository.PermissionRepository;
import com.eze_dev.torneos.service.interfaces.IPermissionService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionResponseDto create(PermissionCreateDto permissionCreateDto) {
        if (permissionRepository.existsByName(permissionCreateDto.getName())) {
            throw new EntityExistsException("Permission with name " + permissionCreateDto.getName() + " already exists.");
        }

        return permissionMapper.toDto(permissionRepository.save(permissionMapper.toEntity(permissionCreateDto)));
    }

    @Override
    public List<PermissionResponseDto> getAll() {
        return permissionRepository.findAll()
                .stream()
                .map(permissionMapper::toDto)
                .toList();
    }

    @Override
    public PermissionResponseDto getById(Long id) {
        return permissionRepository.findById(id)
                .map(permissionMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with ID: " + id));
    }

    @Override
    public PermissionResponseDto update(Long id, PermissionUpdateDto permissionUpdateDto) {
        return permissionRepository.findById(id)
                .map(existingPermission -> {
                    permissionMapper.updateEntityFromDto(permissionUpdateDto, existingPermission);

                    return permissionMapper.toDto(permissionRepository.save(existingPermission));
                })
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with ID: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new EntityNotFoundException("Permission not found with ID: " + id);
        }

        permissionRepository.deleteById(id);
    }
}
