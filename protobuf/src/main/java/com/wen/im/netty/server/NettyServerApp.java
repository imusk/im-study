package com.wen.im.netty.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @classname: NettyServerApp
 * @description: Netty 服务端主程序
 * @data: 2020-07-17 00:59
 * @author: Musk
 */
@SpringBootApplication
public class NettyServerApp {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // 启动嵌入式的 Tomcat 并初始化 Spring 环境及其各 Spring 组件
        ApplicationContext context = SpringApplication.run(NettyServerApp.class, args);
        NettyServer nettyServer = context.getBean(NettyServer.class);
        nettyServer.run();
    }

}