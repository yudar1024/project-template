// file: src/main/java/com/gcl/app/infrastructure/config/StpInterfaceImpl.java
package com.gcl.app.infrastructure.config;

import cn.dev33.satoken.stp.StpInterface;
import com.gcl.app.infrastructure.persistence.mapper.PermissionMapper;
import com.gcl.app.infrastructure.persistence.mapper.RolePermissionMapper;
import com.gcl.app.infrastructure.persistence.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限/角色获取实现
 * Sa-Token 通过此接口获取当前登录用户的权限和角色列表
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    /**
     * 获取用户的权限编码列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);

        List<String> permissions = new ArrayList<>();
        for (Long roleId : roleIds) {
            List<Long> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
            for (Long permissionId : permissionIds) {
                var permission = permissionMapper.selectById(permissionId);
                if (permission != null) {
                    permissions.add(permission.getCode());
                }
            }
        }
        return permissions;
    }

    /**
     * 获取用户的角色标识列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        return roleIds.stream().map(String::valueOf).toList();
    }
}
