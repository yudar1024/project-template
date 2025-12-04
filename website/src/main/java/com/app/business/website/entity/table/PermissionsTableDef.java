package com.app.business.website.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 权限表 表定义层。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
public class PermissionsTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 权限表
     */
    public static final PermissionsTableDef PERMISSIONS = new PermissionsTableDef();

    /**
     * id
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 描述
     */
    public final QueryColumn DESC = new QueryColumn(this, "desc");

    /**
     * 权限名称
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 作用域
     */
    public final QueryColumn SCOPE = new QueryColumn(this, "scope");

    /**
     * 操作
     */
    public final QueryColumn ACTIONS = new QueryColumn(this, "actions");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, DESC, SCOPE, ACTIONS};

    public PermissionsTableDef() {
        super("", "permissions");
    }

    private PermissionsTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public PermissionsTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new PermissionsTableDef("", "permissions", alias));
    }

}
