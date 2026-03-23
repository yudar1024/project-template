// file: src/main/java/com/gcl/app/infrastructure/persistence/entity/RoleEntity.java
package com.gcl.app.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色数据库实体
 */
@Data
@TableName("roles")
public class RoleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    private Long updatedBy;

    /** 角色权限ID列表（非数据库字段） */
    @TableField(exist = false)
    private List<Long> permissionIds;
}
