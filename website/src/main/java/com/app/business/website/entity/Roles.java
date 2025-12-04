package com.app.business.website.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;



/**
 * 角色表 实体类。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
@Table("tb_roles")
public class Roles implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 描述
     */
    private String desc;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
