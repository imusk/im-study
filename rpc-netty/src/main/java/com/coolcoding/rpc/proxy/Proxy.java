package com.coolcoding.rpc.proxy;

import com.coolcoding.rpc.client.NettyClient;

/**
 * 代理接口
 */
public interface Proxy {

    /**
     * 创建一个代理类
     * @param interfaceClass 接口类型
     * @param nettyClient 连接哪个服务
     * @param async 是否异步调用
     * @return 返回目标接口的实例
     */
    <T> T newProxy(Class<T> interfaceClass, NettyClient nettyClient, boolean async);
}
