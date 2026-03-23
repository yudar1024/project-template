// file: src/main/java/com/gcl/app/infrastructure/persistence/entity/UserRoleEntity.java
package com.gcl.app.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户-角色关联实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_roles")
public class UserRoleEntity {

    private Long userId;

    private Long roleId;
}
