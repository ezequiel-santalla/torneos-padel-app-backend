package com.eze_dev.torneos.service.implementations;

import com.eze_dev.torneos.dto.request.create.RoleCreateDto;
import com.eze_dev.torneos.dto.request.update.RoleUpdateDto;
import com.eze_dev.torneos.dto.response.RoleResponseDto;
import com.eze_dev.torneos.mapper.RoleMapper;
import com.eze_dev.torneos.model.Permission;
import com.eze_dev.torneos.model.Role;
import com.eze_dev.torneos.repository.PermissionRepository;
import com.eze_dev.torneos.repository.RoleRepository;
import com.eze_dev.torneos.service.interfaces.IRoleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleResponseDto create(RoleCreateDto roleCreateDto) {
        if (roleRepository.existsByName(roleCreateDto.getName())) {
            throw new IllegalArgumentException("Role with name " + roleCreateDto.getName() + " already exists.");
        }

        Role role = roleMapper.toEntity(roleCreateDto);

        Set<Permission> permissions = getPermissionsByIds(roleCreateDto.getPermissionIds());
        role.setPermissions(permissions);

        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    public List<RoleResponseDto> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toDto)
                .toList();
    }

    @Override
    public RoleResponseDto getById(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID: " + id));
    }

    @Override
    public RoleResponseDto update(Long id, RoleUpdateDto roleUpdateDto) {
        return roleRepository.findById(id)
                .map(existingRole -> {
                    roleMapper.updateEntityFromDto(roleUpdateDto, existingRole);

                    return roleMapper.toDto(roleRepository.save(existingRole));
                })
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("Role not found with ID: " + id);
        }

        roleRepository.deleteById(id);
    }

    private Set<Permission> getPermissionsByIds(Set<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        if (permissions.size() != permissionIds.size()) {
            throw new EntityNotFoundException("Some permissions were not found");
        }

        return new HashSet<>(permissions);
    }
}
