// file: src/main/java/com/gcl/app/JavaAuthApplication.java
package com.gcl.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Java Auth 应用启动类
 */
@SpringBootApplication
@MapperScan("com.gcl.app.infrastructure.persistence.mapper")
public class JavaAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaAuthApplication.class, args);
    }
}
