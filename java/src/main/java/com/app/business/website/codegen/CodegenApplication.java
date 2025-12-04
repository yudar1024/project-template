package com.app.business.website.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.*;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.iotdb.jdbc.IoTDBDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis-Flex 代码生成器
 */
public class CodegenApplication {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(
                "jdbc:mysql://180.213.184.159:3306/website?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai");
        dataSource.setUsername("root");
        dataSource.setPassword("Mysql1024");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        Class.forName("org.apache.iotdb.jdbc.IoTDBDriver");

        IoTDBDataSource ioTDBDataSource = new IoTDBDataSource();
        ioTDBDataSource.setUrl("jdbc:iotdb://localhost:6667/vpp?sql_dialect=table");
        ioTDBDataSource.setUser("root");
        ioTDBDataSource.setPassword("root");
        Connection connection = ioTDBDataSource.getConnection();
        // ResultSet rs= ioTDBDataSource.getConnection().getMetaData().getTables(null,
        // "vpp", null, new String[]{"TABLE"});
        ResultSet rs = connection.getMetaData().getPrimaryKeys(connection.getCatalog(), "vpp", "tb_dian_biao");

        while (rs.next()) {
            System.out.println(rs.getString("TABLE_NAME") + " " + rs.getString("COLUMN_NAME"));
        }

        GlobalConfig globalConfig = createGlobalConfig();

        // 通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new MyGenerator(ioTDBDataSource, globalConfig, new IotDbJdbcDialect());
        createPackageConfig(generator.getGlobalConfig().getPackageConfig());
        createStrategyConfig(generator.getGlobalConfig().getStrategyConfig(), "vpp");
        createMaperConfig(generator.getGlobalConfig().getMapperConfig());

        // 生成代码
        generator.generate();
    }

    /**
     * 创建全局配置
     */
    public static GlobalConfig createGlobalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        // 作者
        globalConfig.setAuthor("CodeGenerator");

        // 去掉表前缀
        globalConfig.setTablePrefix("tb_", "t_");
        globalConfig.setUnGenerateTable("flyway_schema_history");

        // 设置生成 entity 并启用 Lombok
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(false);

        // 设置生成 mapper
        globalConfig.setMapperGenerateEnable(true);

        // 设置生成 service
        globalConfig.setServiceGenerateEnable(true);
        globalConfig.setServiceImplGenerateEnable(true);

        // 设置生成 controller
        globalConfig.setControllerGenerateEnable(true);

        // 设置生成 TableDef
        globalConfig.setTableDefGenerateEnable(true);

        // 设置生成 MapperXml
        globalConfig.setMapperXmlGenerateEnable(true);

        // 设置生成 package-info.java
        globalConfig.setPackageInfoGenerateEnable(true);
        globalConfig.setVersionColumn("version");
        globalConfig.setEntityOverwriteEnable(true);
        globalConfig.setMapperOverwriteEnable(true);
        globalConfig.setServiceOverwriteEnable(true);
        globalConfig.setServiceImplOverwriteEnable(true);
        globalConfig.setControllerOverwriteEnable(true);
        globalConfig.setTableDefOverwriteEnable(true);
        globalConfig.setMapperXmlOverwriteEnable(true);

        return globalConfig;
    }

    /**
     * 创建包配置
     */
    public static PackageConfig createPackageConfig(PackageConfig packageConfig) {

        // 根包
        packageConfig.setBasePackage("com.app.business.website");

        // 实体类包名
        packageConfig.setEntityPackage(packageConfig.getBasePackage() + ".entity");

        // Mapper 包名
        packageConfig.setMapperPackage(packageConfig.getBasePackage() + ".mapper");

        // Service 包名
        packageConfig.setServicePackage(packageConfig.getBasePackage() + ".service");
        packageConfig.setServiceImplPackage(packageConfig.getBasePackage() + ".service.impl");

        // Controller 包名
        packageConfig.setControllerPackage(packageConfig.getBasePackage() + ".controller");

        // TableDef 包名
        packageConfig.setTableDefPackage("com.app.business.website.entity.table");

        // MapperXml 路径
        packageConfig.setMapperXmlPath("/src/main/resources/mapper");

        return packageConfig;
    }

    public static StrategyConfig createStrategyConfig(StrategyConfig strategyConfig, String schema) {
        strategyConfig.setGenerateSchema(schema);
        return createStrategyConfig(strategyConfig);
    }

    /**
     * 创建策略配置
     */
    public static StrategyConfig createStrategyConfig(StrategyConfig strategyConfig) {
        // strategyConfig = new StrategyConfig();
        // 设置需要生成的表名，可以通过正则表达式匹配
        // strategyConfig.setGenerateTable("tb_.*");
        // 设置需要生成的表名,当不配置表名时，生成全部表
        // strategyConfig.setGenerateTable("tb_user", "tb_role", "tb_permission");

        // 忽略的表名
        // strategyConfig.setIgnoreTable("ignore_table");

        // 字段配置示例
        // 配置通用字段 created_at 使用 @TableField(onInsertValue = "now()")

        ColumnConfig createdAtColumn = new ColumnConfig();
        createdAtColumn.setColumnName("created_time");
        createdAtColumn.setOnInsertValue("NOW()");
        strategyConfig.setColumnConfig("*", createdAtColumn);

        // 配置通用字段 updated_at 使用 @TableField(onUpdateValue = "now()")
        ColumnConfig updatedAtColumn = new ColumnConfig();
        updatedAtColumn.setColumnName("updated_at");
        updatedAtColumn.setOnUpdateValue("NOW()");
        strategyConfig.setColumnConfig("*", updatedAtColumn);

        return strategyConfig;
    }

    public static void createMaperConfig(MapperConfig mapperConfig) {

    }

}