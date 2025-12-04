package com.app.business.website.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.StrategyConfig;
import com.mybatisflex.codegen.dialect.IDialect;
import com.mybatisflex.codegen.entity.Table;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyGenerator extends Generator {
    public MyGenerator(DataSource dataSource, GlobalConfig globalConfig) {
        super(dataSource, globalConfig);
    }

    public MyGenerator(DataSource dataSource, GlobalConfig globalConfig, IDialect dialect) {
        super(dataSource, globalConfig, dialect);
    }
    public void buildPrimaryKey(DatabaseMetaData dbMeta, Connection conn, Table table,String schema) throws SQLException {
        try (ResultSet rs = dbMeta.getPrimaryKeys(conn.getCatalog(), schema, table.getName())) {
            while(rs.next()) {
                String primaryKey = rs.getString("COLUMN_NAME");
                table.addPrimaryKey(primaryKey);
            }
        }

    }

    @Override
    protected List<Table> buildTables(DatabaseMetaData dbMeta, Connection conn) throws SQLException {
        StrategyConfig strategyConfig = this.globalConfig.getStrategyConfig();
        String schemaName = strategyConfig.getGenerateSchema();
        List<Table> tables = new ArrayList();

        try (ResultSet rs = this.getTablesResultSet(dbMeta, conn, schemaName)) {
            while(rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (strategyConfig.isSupportGenerate(tableName)) {
                    Table table = new Table();
                    table.setGlobalConfig(this.globalConfig);
                    table.setTableConfig(strategyConfig.getTableConfig(tableName));
                    table.setEntityConfig(this.globalConfig.getEntityConfig());
                    table.setSchema(schemaName);
                    table.setName(tableName);
                    String remarks = rs.getString("REMARKS");
                    table.setComment(remarks);
                    this.buildPrimaryKey(dbMeta, conn, table,schemaName);
                    this.dialect.buildTableColumns(schemaName, table, this.globalConfig, dbMeta, conn);
                    tables.add(table);
                }
            }
        }

        return tables;
    }
}
