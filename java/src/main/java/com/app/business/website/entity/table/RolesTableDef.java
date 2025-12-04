package com.app.business.website.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 角色表 表定义层。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
public class RolesTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 角色表
     */
    public static final RolesTableDef ROLES = new RolesTableDef();

    /**
     * ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 描述
     */
    public final QueryColumn DESC = new QueryColumn(this, "desc");

    /**
     * 角色名称
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, DESC};

    public RolesTableDef() {
        super("", "tb_roles");
    }

    private RolesTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public RolesTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new RolesTableDef("", "tb_roles", alias));
    }

}
