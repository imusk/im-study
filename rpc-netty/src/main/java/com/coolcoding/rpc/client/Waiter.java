package com.coolcoding.rpc.client;

import com.coolcoding.rpc.model.RpcResponse;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Waiter {

    private static Map<Long, BlockingQueue<RpcResponse>> map = new ConcurrentHashMap<>();


    public static void putQueue(Long id, BlockingQueue<RpcResponse> queue) {
        map.put(id, queue);
    }

    public static BlockingQueue<RpcResponse> getQueue(Long id) {
        return map.get(id);
    }

    public static void removeQueue(Long id) {
        map.remove(id);
    }
}
