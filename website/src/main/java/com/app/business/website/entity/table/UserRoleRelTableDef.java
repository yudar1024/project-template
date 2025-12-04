package com.app.business.website.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 用户角色关联表 表定义层。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
public class UserRoleRelTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 用户角色关联表
     */
    public static final UserRoleRelTableDef USER_ROLE_REL = new UserRoleRelTableDef();

    /**
     * ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 预留扩展字段1
     */
    public final QueryColumn EXT1 = new QueryColumn(this, "ext1");

    /**
     * 角色id
     */
    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");

    /**
     * 用户id
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USER_ID, ROLE_ID, EXT1};

    public UserRoleRelTableDef() {
        super("", "user_role_rel");
    }

    private UserRoleRelTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public UserRoleRelTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new UserRoleRelTableDef("", "user_role_rel", alias));
    }

}
