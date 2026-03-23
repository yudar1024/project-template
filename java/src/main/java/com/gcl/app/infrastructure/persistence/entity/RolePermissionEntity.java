// file: src/main/java/com/gcl/app/infrastructure/persistence/entity/RolePermissionEntity.java
package com.gcl.app.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色-权限关联实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("role_permissions")
public class RolePermissionEntity {

    private Long roleId;

    private Long permissionId;
}
