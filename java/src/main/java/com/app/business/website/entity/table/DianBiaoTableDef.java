package com.app.business.website.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


/**
 * 电表设备 表定义层。
 *
 * @author CodeGenerator
 * @since 2025-11-24
 */
public class DianBiaoTableDef extends TableDef {

    private static final long serialVersionUID = 1L;

    /**
     * 电表设备
     */
    public static final DianBiaoTableDef DIAN_BIAO = new DianBiaoTableDef();

    
    public final QueryColumn TIME = new QueryColumn(this, "time");

    
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    
    public final QueryColumn DEVICE_ID = new QueryColumn(this, "device_id");

    
    public final QueryColumn ELEC_USED = new QueryColumn(this, "elec_used");

    
    public final QueryColumn MAINTENANCE = new QueryColumn(this, "maintenance");

    
    public final QueryColumn TEMPERATURE = new QueryColumn(this, "temperature");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{TIME, DEVICE_ID, MAINTENANCE, TEMPERATURE, ELEC_USED, STATUS};

    public DianBiaoTableDef() {
        super("vpp", "tb_dian_biao");
    }

    private DianBiaoTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public DianBiaoTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new DianBiaoTableDef("vpp", "tb_dian_biao", alias));
    }

}
