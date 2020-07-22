package com.coolcoding.rpc.example;

import com.coolcoding.rpc.client.NettyClient;
import com.coolcoding.rpc.future.RpcResponseFuture;
import com.coolcoding.rpc.proxy.Proxy;
import com.coolcoding.rpc.proxy.ProxyFactory;

public class CalculatorClient {

    public static void main(String[] args) throws Exception {

        // 启动客户端
        NettyClient nettyClient = new NettyClient();
        nettyClient.start("localhost", 8080);

        Proxy proxy = ProxyFactory.getProxy();

        // 同步调用
        CalculatorService calculatorService = proxy.newProxy(CalculatorService.class, nettyClient, false);
        System.out.println("sync: " + calculatorService.add(1D, 2D));

        // 同步调用
        System.out.println("sync: " + calculatorService.sub(1D, 2D));

        // 异步调用
        calculatorService = proxy.newProxy(CalculatorService.class, nettyClient, true);
        System.out.println("async1: " + calculatorService.mul(22.8D, 30.5D));
        RpcResponseFuture rpcResponseFuture = RpcResponseFuture.getRpcResponseFuture();
        System.out.println("async1: " + rpcResponseFuture.get());

        // 异步调用
        System.out.println("async1: " + calculatorService.div(22.8D, 30.5D));
        rpcResponseFuture = RpcResponseFuture.getRpcResponseFuture();
        System.out.println("async1: " + rpcResponseFuture.get());

    }
}
