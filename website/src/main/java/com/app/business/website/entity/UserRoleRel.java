package com.app.business.website.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;



/**
 * 用户角色关联表 实体类。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
@Table("user_role_rel")
public class UserRoleRel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 预留扩展字段1
     */
    private String ext1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

}
