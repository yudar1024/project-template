package com.app.business.website.codegen;

import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.dialect.AbstractJdbcDialect;
import com.mybatisflex.codegen.entity.Column;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.core.util.StringUtil;

import java.sql.*;
public class IotDbJdbcDialect extends AbstractJdbcDialect {

    @Override
    protected String forBuildColumnsSql(String schema, String tableName) {
        return "SELECT * FROM " + (StringUtil.hasText(schema) ? schema + "." : "") + tableName + " WHERE 1 = 2";
    }

    @Override
    public void buildTableColumns(String schema, Table table, GlobalConfig globalConfig, DatabaseMetaData dbMeta,
            Connection conn) throws SQLException {
        String sql = forBuildColumnsSql(schema, table.getName());
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);

                Column column = new Column();
                column.setName(columnName);

                String jdbcTypeName = metaData.getColumnTypeName(i);
                int jdbcType = metaData.getColumnType(i);

                column.setPropertyType(resultSetTypeToJavaType(columnName, jdbcTypeName, jdbcType));

                table.addColumn(column);
            }
        }
    }

    public String resultSetTypeToJavaType(String columnClassName, String jdbcTypeName, int jdbcType) {
        if ("BOOLEAN".equalsIgnoreCase(jdbcTypeName)) {
            return "java.lang.Boolean";
        } else if ("INT32".equalsIgnoreCase(jdbcTypeName)) {
            return "java.lang.Integer";
        } else if ("INT64".equalsIgnoreCase(jdbcTypeName)) {
            return "java.lang.Long";
        } else if ("FLOAT".equalsIgnoreCase(jdbcTypeName)) {
            return "java.lang.Float";
        } else if ("DOUBLE".equalsIgnoreCase(jdbcTypeName)) {
            return "java.lang.Double";
        } else if ("TEXT".equalsIgnoreCase(jdbcTypeName) || "STRING".equalsIgnoreCase(jdbcTypeName)) {
            return "java.lang.String";
        } else if ("TIMESTAMP".equalsIgnoreCase(jdbcTypeName)) {
            return "java.sql.Timestamp";
        } else if ("DATE".equalsIgnoreCase(jdbcTypeName)) {
            return "java.sql.Date";
        } else if ("BLOB".equalsIgnoreCase(jdbcTypeName)) {
            return "byte[]";
        }
        return "java.lang.String";
    }
}
