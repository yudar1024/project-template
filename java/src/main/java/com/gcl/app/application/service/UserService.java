// file: src/main/java/com/gcl/app/application/service/UserService.java
package com.gcl.app.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gcl.app.application.dto.request.UserCreateRequest;
import com.gcl.app.application.dto.request.UserUpdateRequest;
import com.gcl.app.application.dto.response.UserResponse;
import com.gcl.app.common.exception.BusinessException;
import com.gcl.app.common.exception.ErrorCode;
import com.gcl.app.common.response.PageResponse;
import com.gcl.app.common.util.PasswordUtil;
import com.gcl.app.infrastructure.persistence.entity.UserEntity;
import com.gcl.app.infrastructure.persistence.entity.UserRoleEntity;
import com.gcl.app.infrastructure.persistence.mapper.UserMapper;
import com.gcl.app.infrastructure.persistence.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户管理服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    /**
     * 用户分页列表
     */
    public PageResponse<UserResponse> listUsers(long page, long size) {
        IPage<UserEntity> pageResult = userMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<UserEntity>().orderByDesc(UserEntity::getCreatedAt)
        );

        List<UserResponse> items = pageResult.getRecords().stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.of(items, pageResult.getTotal(), page, size);
    }

    /**
     * 创建用户
     */
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // 检查用户名是否已存在
        long count = userMapper.selectCount(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUsername, request.getUserName())
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.USER_EXISTS, "用户名已存在: " + request.getUserName());
        }

        // 创建用户实体
        UserEntity entity = new UserEntity();
        entity.setUsername(request.getUserName());
        entity.setPhone(request.getMobile());
        entity.setEmail(request.getEmail());
        entity.setPassword(PasswordUtil.encode(request.getPassword()));
        entity.setNickname(request.getNickName());
        entity.setStatus("active");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(entity);

        // 保存用户-角色关联
        saveUserRoles(entity.getId(), request.getUserRoleList());

        log.info("用户创建成功, userId={}, username={}", entity.getId(), entity.getUsername());
        return toResponse(entity, request.getUserRoleList());
    }

    /**
     * 更新用户
     */
    @Transactional
    public UserResponse updateUser(UserUpdateRequest request) {
        UserEntity existing = userMapper.selectById(request.getId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在: " + request.getId());
        }

        // 部分更新
        if (request.getUserName() != null) {
            existing.setUsername(request.getUserName());
        }
        if (request.getMobile() != null) {
            existing.setPhone(request.getMobile());
        }
        if (request.getEmail() != null) {
            existing.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            existing.setPassword(PasswordUtil.encode(request.getPassword()));
        }
        if (request.getNickName() != null) {
            existing.setNickname(request.getNickName());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(existing);

        // 更新用户-角色关联
        if (request.getUserRoleList() != null) {
            userRoleMapper.delete(
                    new LambdaQueryWrapper<UserRoleEntity>()
                            .eq(UserRoleEntity::getUserId, existing.getId())
            );
            saveUserRoles(existing.getId(), request.getUserRoleList());
        }

        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(existing.getId());
        log.info("用户更新成功, userId={}", existing.getId());
        return toResponse(existing, roleIds);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        UserEntity existing = userMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在: " + id);
        }

        // 删除用户-角色关联
        userRoleMapper.delete(
                new LambdaQueryWrapper<UserRoleEntity>()
                        .eq(UserRoleEntity::getUserId, id)
        );

        userMapper.deleteById(id);
        log.info("用户删除成功, userId={}", id);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                userRoleMapper.insert(new UserRoleEntity(userId, roleId));
            }
        }
    }

    private UserResponse toResponse(UserEntity entity) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(entity.getId());
        return toResponse(entity, roleIds);
    }

    private UserResponse toResponse(UserEntity entity, List<Long> roleIds) {
        return UserResponse.builder()
                .id(entity.getId())
                .userName(entity.getUsername())
                .mobile(entity.getPhone())
                .email(entity.getEmail())
                .nickName(entity.getNickname())
                .status(entity.getStatus())
                .userRoleList(roleIds)
                .createTime(entity.getCreatedAt())
                .updateTime(entity.getUpdatedAt())
                .createUser(entity.getCreatedBy())
                .updateUser(entity.getUpdatedBy())
                .build();
    }
}
