package com.wen.im.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
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
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, FreeMarkerAutoConfiguration.class})
public class H2Application {

    private static final Logger logger = LoggerFactory.getLogger(H2Application.class);

    public static void main(String[] args) {
        Instant inst1 = Instant.now();
        SpringApplication.run(H2Application.class, args);

        logger.info("H2 平台 : 启动成功 , 耗时 : {} 秒", Duration.between(inst1, Instant.now()).getSeconds());

    }

}