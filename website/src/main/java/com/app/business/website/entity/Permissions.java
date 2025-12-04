package com.app.business.website.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;



/**
 * 权限表 实体类。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
@Table("permissions")
public class Permissions implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 描述
     */
    private Integer desc;

    /**
     * 作用域
     */
    private String scope;

    /**
     * 操作
     */
    private String actions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDesc() {
        return desc;
    }

    public void setDesc(Integer desc) {
        this.desc = desc;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

}
