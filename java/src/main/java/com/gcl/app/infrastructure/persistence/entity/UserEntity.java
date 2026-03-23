// file: src/main/java/com/gcl/app/infrastructure/persistence/entity/UserEntity.java
package com.gcl.app.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户数据库实体
 */
@Data
@TableName("users")
public class UserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String phone;

    private String email;

    private String password;

    private String nickname;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    private Long updatedBy;

    /** 用户角色ID列表（非数据库字段） */
    @TableField(exist = false)
    private java.util.List<Long> roleIds;
}
