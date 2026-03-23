// file: src/main/java/com/gcl/app/infrastructure/persistence/entity/PermissionEntity.java
package com.gcl.app.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限数据库实体
 */
@Data
@TableName("permissions")
public class PermissionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private String type;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    private Long updatedBy;
}
