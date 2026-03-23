// file: src/main/java/com/gcl/app/infrastructure/persistence/entity/MenuEntity.java
package com.gcl.app.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单数据库实体
 */
@Data
@TableName("menus")
public class MenuEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String path;

    private Long parentId;

    private String code;

    private String type;

    private Integer level;

    private Integer sort;

    private String icon;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    private Long updatedBy;

    /** 子菜单列表（非数据库字段） */
    @TableField(exist = false)
    private List<MenuEntity> children;
}
