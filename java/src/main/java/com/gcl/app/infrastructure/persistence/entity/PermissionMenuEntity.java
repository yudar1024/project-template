// file: src/main/java/com/gcl/app/infrastructure/persistence/entity/PermissionMenuEntity.java
package com.gcl.app.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限-菜单关联实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("permission_menus")
public class PermissionMenuEntity {

    private Long permissionId;

    private Long menuId;
}
