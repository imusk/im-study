package com.github.imusk.im.study.protobuf.netty.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @classname: NettyClientApp
 * @description: Netty 客户端主程序
 * @data: 2020-07-17 00:57
 * @author: Musk
 */
@SpringBootApplication
public class NettyClientApp {

    public static void main(String[] args) {
        // 启动嵌入式的 Tomcat 并初始化 Spring 环境及其各 Spring 组件
        ApplicationContext context = SpringApplication.run(NettyClientApp.class, args);
        NettyClient nettyClient = context.getBean(NettyClient.class);
        nettyClient.run();
    }
}
