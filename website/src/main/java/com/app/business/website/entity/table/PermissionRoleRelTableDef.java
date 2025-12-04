package com.app.business.website.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
public class PermissionRoleRelTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final PermissionRoleRelTableDef PERMISSION_ROLE_REL = new PermissionRoleRelTableDef();

    /**
     * id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 扩展字段
     */
    public final QueryColumn EXT1 = new QueryColumn(this, "ext1");

    /**
     * 角色id
     */
    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");

    /**
     * 权限id
     */
    public final QueryColumn PERMISSION_ID = new QueryColumn(this, "permission_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ROLE_ID, PERMISSION_ID, EXT1};

    public PermissionRoleRelTableDef() {
        super("", "permission_role_rel");
    }

    private PermissionRoleRelTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public PermissionRoleRelTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new PermissionRoleRelTableDef("", "permission_role_rel", alias));
    }

}
