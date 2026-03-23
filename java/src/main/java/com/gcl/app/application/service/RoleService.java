// file: src/main/java/com/gcl/app/application/service/RoleService.java
package com.gcl.app.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gcl.app.application.dto.request.RoleCreateRequest;
import com.gcl.app.application.dto.request.RoleUpdateRequest;
import com.gcl.app.application.dto.response.RoleResponse;
import com.gcl.app.common.exception.BusinessException;
import com.gcl.app.common.exception.ErrorCode;
import com.gcl.app.infrastructure.persistence.entity.RoleEntity;
import com.gcl.app.infrastructure.persistence.entity.RolePermissionEntity;
import com.gcl.app.infrastructure.persistence.mapper.RoleMapper;
import com.gcl.app.infrastructure.persistence.mapper.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色管理服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    /**
     * 角色列表
     */
    public List<RoleResponse> listRoles() {
        List<RoleEntity> roles = roleMapper.selectList(
                new LambdaQueryWrapper<RoleEntity>().orderByAsc(RoleEntity::getId)
        );
        return roles.stream().map(this::toResponse).toList();
    }

    /**
     * 创建角色
     */
    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        // 检查角色名是否已存在
        long count = roleMapper.selectCount(
                new LambdaQueryWrapper<RoleEntity>()
                        .eq(RoleEntity::getName, request.getRoleName())
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.ROLE_EXISTS, "角色名已存在: " + request.getRoleName());
        }

        RoleEntity entity = new RoleEntity();
        entity.setName(request.getRoleName());
        entity.setStatus("active");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        roleMapper.insert(entity);

        // 保存角色-权限关联
        saveRolePermissions(entity.getId(), request.getRolePermissionList());

        log.info("角色创建成功, roleId={}, roleName={}", entity.getId(), entity.getName());
        return toResponse(entity, request.getRolePermissionList());
    }

    /**
     * 更新角色
     */
    @Transactional
    public RoleResponse updateRole(RoleUpdateRequest request) {
        RoleEntity existing = roleMapper.selectById(request.getId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND, "角色不存在: " + request.getId());
        }

        if (request.getRoleName() != null) {
            existing.setName(request.getRoleName());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateById(existing);

        // 更新角色-权限关联
        if (request.getRolePermissionList() != null) {
            rolePermissionMapper.delete(
                    new LambdaQueryWrapper<RolePermissionEntity>()
                            .eq(RolePermissionEntity::getRoleId, existing.getId())
            );
            saveRolePermissions(existing.getId(), request.getRolePermissionList());
        }

        List<Long> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleId(existing.getId());
        log.info("角色更新成功, roleId={}", existing.getId());
        return toResponse(existing, permissionIds);
    }

    /**
     * 删除角色
     */
    @Transactional
    public void deleteRole(Long id) {
        RoleEntity existing = roleMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND, "角色不存在: " + id);
        }

        rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermissionEntity>()
                        .eq(RolePermissionEntity::getRoleId, id)
        );
        roleMapper.deleteById(id);
        log.info("角色删除成功, roleId={}", id);
    }

    private void saveRolePermissions(Long roleId, List<Long> permissionIds) {
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                rolePermissionMapper.insert(new RolePermissionEntity(roleId, permissionId));
            }
        }
    }

    private RoleResponse toResponse(RoleEntity entity) {
        List<Long> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleId(entity.getId());
        return toResponse(entity, permissionIds);
    }

    private RoleResponse toResponse(RoleEntity entity, List<Long> permissionIds) {
        return RoleResponse.builder()
                .id(entity.getId())
                .roleName(entity.getName())
                .rolePermissionList(permissionIds)
                .createTime(entity.getCreatedAt())
                .updateTime(entity.getUpdatedAt())
                .createUser(entity.getCreatedBy())
                .updateUser(entity.getUpdatedBy())
                .build();
    }
}
