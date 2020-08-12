package com.github.imusk.im.study.flyway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;
import java.time.Instant;

/**
 * 启动程序
 */
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
public class FlywayApplication {

    private static final Logger logger = LoggerFactory.getLogger(FlywayApplication.class);

    public static void main(String[] args) {
        Instant inst1 = Instant.now();
        SpringApplication.run(FlywayApplication.class, args);

        logger.info("Flyway 平台 : 启动成功 , 耗时 : {} 秒", Duration.between(inst1, Instant.now()).getSeconds());

    }

}