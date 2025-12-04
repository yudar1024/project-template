package com.app.business.website.config.logging;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * 日志配置类
 * 参照 JHipster 最佳实践
 */
@Configuration
@ConfigurationProperties(prefix = "logging")
public class LoggingConfiguration {

    private final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    @PostConstruct
    public void init() {
        // 可以在这里添加自定义的日志初始化逻辑
    }

    /**
     * 动态设置日志级别
     */
    public void setLogLevel(String loggerName, String level) {
        ch.qos.logback.classic.Logger logger = context.getLogger(loggerName);
        logger.setLevel(ch.qos.logback.classic.Level.valueOf(level));
    }
}