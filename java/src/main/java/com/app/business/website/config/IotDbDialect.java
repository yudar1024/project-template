package com.app.business.website.config;

import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.QueryWrapper;

public class IotDbDialect extends CommonsDialectImpl {

    public IotDbDialect() {
        // 1. IoTDB uses backticks for keyword wrapping (similar to MySQL)
        super(KeywordWrap.NONE, LimitOffsetProcessor.MYSQL);
    }

    /**
     * IoTDB Specific Wrapping Logic.
     * 
     * IoTDB tables are actually "Device Paths" (e.g., root.sg.d
     * We generally do not want to wrap the entire path in backticks (e.g., `r
     * but we should wrap individual nodes if they are keywords.
     * 
     * * However, for simplicity in an ORM context, usually we assume the user
     * provides the correct table name. If strictly needed, we wrap standard
     * identifiers.
     */
    @Override
    public String wrap(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return "";
            //
        }
        // If it already contains dots (root.sg.d1), it's a path; avoid wrapping the
        // whole string.
        if (keyword.contains(".")) {
            return keyword;
        }
        // If it's the wildcard, don't wrap
        if ("*".equals(keyword)) {
            return keyword;
        }
        return super.wrap(keyword);
    }

    /**
     * Customize the SELECT generation if necessary.
     * IoTDB supports: SELECT s1, s2 FROM root.sg.d1 WHERE time > 100
     */
    @Override
    public String buildSelectSql(QueryWrapper queryWrapper) {
        // IoTDB SQL structure is compatible with the default buildSelectSql
        // generic implementation provided by CommonsDialectImpl.
        return super.buildSelectSql(queryWrapper);
    }

    /**
     * IoTDB Pagination: LIMIT N OFFSET M
     * 
     * 
     */

    /**
     * Handle Insert SQL.
     * IoTDB: INSERT INTO root.ln(timestamp, s1) VALUES(?, ?)
     * * Note: MyBatis-Flex automatically handles the generation of column names
     * and values. The standard SQL generation works for IoTDB
     * provided the entity maps 'time' correctly.
     */
}