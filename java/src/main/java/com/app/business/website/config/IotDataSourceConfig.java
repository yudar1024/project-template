package com.app.business.website.config;

import com.app.business.website.codegen.IotDbJdbcDialect;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IotDataSourceConfig {


    @Bean
    IotDbDialect iotDbDialect(){
        return new IotDbDialect();
    }
    @Bean
    IotDbJdbcDialect iotDbJdbcDialect(){
        return new IotDbJdbcDialect();
    }

}
