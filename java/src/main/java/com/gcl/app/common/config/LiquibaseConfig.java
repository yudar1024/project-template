package com.gcl.app.common.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
        liquibase.setShouldRun(true);
        // 如果使用的是 MySQL，建议设置数据库不支持 DDL 时的默认 schema，或者留空
        // liquibase.setDefaultSchema("java_auth");
        return liquibase;
    }
}
