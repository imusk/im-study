package com.coolcoding.rpc.invoker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceHolder {

    private static Map<String, Object> map = new ConcurrentHashMap<>();

    public static void publishService(Class<?> serviceClz, Object service) {
        map.put(serviceClz.getName(), service);
    }

    public static Object getService(String serviceName) {
        return map.get(serviceName);
    }
}
