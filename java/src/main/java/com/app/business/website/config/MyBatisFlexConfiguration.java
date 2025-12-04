package com.app.business.website.config;

import org.springframework.context.annotation.Configuration;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;

@Configuration
public class MyBatisFlexConfiguration implements MyBatisFlexCustomizer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger("Mybatis-flex-sql");

    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        // TODO Auto-generated method stub
        // Register the new dialect
        // You can register it by a keyword key or set it as the default
        DialectFactory.registerDialect(DbType.OTHER, new IotDbDialect());
        // 审计SQL
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(auditMessage -> {
            log.info("{},{}ms", auditMessage.getFullSql(), auditMessage.getElapsedTime());
        });

    }

}
