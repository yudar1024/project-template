package com.app.business.website.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 *  表定义层。
 *
 * @author CodeGenerator
 * @since 2025-10-20
 */
public class UsersTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final UsersTableDef USERS = new UsersTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn PHONE = new QueryColumn(this, "phone");

    
    public final QueryColumn VERSION = new QueryColumn(this, "version");

    
    public final QueryColumn USER_NAME = new QueryColumn(this, "user_name");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USER_NAME, PHONE, VERSION};

    public UsersTableDef() {
        super("", "tb_users");
    }

    private UsersTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public UsersTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new UsersTableDef("", "tb_users", alias));
    }

}
