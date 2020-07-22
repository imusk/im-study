package com.coolcoding.rpc.proxy;

public class ProxyFactory {
    public static Proxy getProxy() {
        return new JdkProxy();
    }
}
