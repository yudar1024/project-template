// file: src/main/java/com/gcl/app/application/service/PermissionService.java
package com.gcl.app.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gcl.app.application.dto.request.PermissionCreateRequest;
import com.gcl.app.application.dto.request.PermissionUpdateRequest;
import com.gcl.app.application.dto.response.PermissionResponse;
import com.gcl.app.common.exception.BusinessException;
import com.gcl.app.common.exception.ErrorCode;
import com.gcl.app.infrastructure.persistence.entity.PermissionEntity;
import com.gcl.app.infrastructure.persistence.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限管理服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final PermissionMapper permissionMapper;

    /**
     * 权限列表
     */
    public List<PermissionResponse> listPermissions() {
        List<PermissionEntity> permissions = permissionMapper.selectList(
                new LambdaQueryWrapper<PermissionEntity>().orderByAsc(PermissionEntity::getId)
        );
        return permissions.stream().map(this::toResponse).toList();
    }

    /**
     * 创建权限
     */
    @Transactional
    public PermissionResponse createPermission(PermissionCreateRequest request) {
        // 检查权限编码是否已存在
        long count = permissionMapper.selectCount(
                new LambdaQueryWrapper<PermissionEntity>()
                        .eq(PermissionEntity::getCode, request.getPermissionExp())
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.PERMISSION_EXISTS,
                    "权限编码已存在: " + request.getPermissionExp());
        }

        PermissionEntity entity = new PermissionEntity();
        entity.setName(request.getPermissionName());
        entity.setCode(request.getPermissionExp());
        entity.setType(request.getPermissionType());
        entity.setStatus("active");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        permissionMapper.insert(entity);

        log.info("权限创建成功, permissionId={}, name={}", entity.getId(), entity.getName());
        return toResponse(entity);
    }

    /**
     * 更新权限
     */
    @Transactional
    public PermissionResponse updatePermission(PermissionUpdateRequest request) {
        PermissionEntity existing = permissionMapper.selectById(request.getId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.PERMISSION_NOT_FOUND,
                    "权限不存在: " + request.getId());
        }

        if (request.getPermissionName() != null) {
            existing.setName(request.getPermissionName());
        }
        if (request.getPermissionExp() != null) {
            existing.setCode(request.getPermissionExp());
        }
        if (request.getPermissionType() != null) {
            existing.setType(request.getPermissionType());
        }
        if (request.getVersion() != null) {
            // version 可存到扩展字段或 code 中
        }
        existing.setUpdatedAt(LocalDateTime.now());
        permissionMapper.updateById(existing);

        log.info("权限更新成功, permissionId={}", existing.getId());
        return toResponse(existing);
    }

    /**
     * 删除权限
     */
    @Transactional
    public void deletePermission(Long id) {
        PermissionEntity existing = permissionMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.PERMISSION_NOT_FOUND, "权限不存在: " + id);
        }

        permissionMapper.deleteById(id);
        log.info("权限删除成功, permissionId={}", id);
    }

    private PermissionResponse toResponse(PermissionEntity entity) {
        return PermissionResponse.builder()
                .id(entity.getId())
                .permissionName(entity.getName())
                .permissionExp(entity.getCode())
                .permissionType(entity.getType())
                .createTime(entity.getCreatedAt())
                .updateTime(entity.getUpdatedAt())
                .createUser(entity.getCreatedBy())
                .updateUser(entity.getUpdatedBy())
                .build();
    }
}
