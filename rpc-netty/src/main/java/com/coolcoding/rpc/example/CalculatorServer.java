package com.coolcoding.rpc.example;

import com.coolcoding.rpc.invoker.ServiceHolder;
import com.coolcoding.rpc.server.NettyServer;

public class CalculatorServer {
    public static void main(String[] args) {
        // 发布一个服务
        ServiceHolder.publishService(CalculatorService.class, new CalculatorServiceImpl());

        // 调动服务端
        new NettyServer(8080).start();
    }
}
