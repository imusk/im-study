package com.github.imusk.im.study.protobuf.rabbitmq.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @classname: ConnectionUtil
 * @description: 用于创建连接的工具类
 * @data: 2020-07-17 00:49
 * @author: Musk
 */
public class ConnectionUtil {

    public static Connection getConnection () throws  Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");//设置 server 的地址
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root123");
        //connectionFactory.setVirtualHost("/test"); need to comment, or will cause error
        return connectionFactory.newConnection();//创建一个新的连接
    }

}
