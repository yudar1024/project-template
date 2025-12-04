package com.app.business.website.config;


import com.mybatisflex.core.datasource.FlexDataSource;
import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class FlywayConfig {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FlywayConfig.class);

    private final DataSource dataSource;

    @Value("${spring.flyway.locations}")
    private String SQL_LOCATION;

    @Value("${spring.flyway.table}")
    private String VERSION_TABLE;

    @Value("${spring.flyway.baseline-on-migrate}")
    private boolean BASELINE_ON_MIGRATE;

    @Value("${spring.flyway.out-of-order}")
    private boolean OUT_OF_ORDER;

    @Value("${spring.flyway.validate-on-migrate}")
    private boolean VALIDATE_ON_MIGRATE;

    public FlywayConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

//    @Bean
    @PostConstruct
    public void migrateOrder() {
        log.info("调用数据库生成工具");
        FlexDataSource ds = (FlexDataSource) dataSource;
        Map<String, DataSource> dataSources = ds.getDataSourceMap();
        dataSources.forEach((k, v) -> {
            log.info("正在执行多数据源生成数据库文件： " + k);

            if (k.equals("ds1")) {
                log.info("正在执行mysql库数据源生成数据库文件");
                // 将路径转换
                SQL_LOCATION = SQL_LOCATION + "/mysql";
                Flyway flyway = Flyway.configure()
                        .dataSource(v)
                        .locations(SQL_LOCATION)
                        .baselineOnMigrate(BASELINE_ON_MIGRATE)
                        .table(VERSION_TABLE)
                        .outOfOrder(OUT_OF_ORDER)
                        .validateOnMigrate(VALIDATE_ON_MIGRATE)
                        .load();
                flyway.migrate();
            } else {
                log.info("正在忽略IOTDB库数据源生成数据库文件");
//                // 将路径转换
//                SQL_LOCATION = SQL_LOCATION + "/iotdb";
//                Flyway flyway = Flyway.configure()
//                        .dataSource(v)
//                        .locations(SQL_LOCATION)
//                        .baselineOnMigrate(BASELINE_ON_MIGRATE)
//                        .table(VERSION_TABLE)
//                        .outOfOrder(OUT_OF_ORDER)
//                        .validateOnMigrate(VALIDATE_ON_MIGRATE)
//                        .load();
//                flyway.migrate();
            }
        });
    }
}
