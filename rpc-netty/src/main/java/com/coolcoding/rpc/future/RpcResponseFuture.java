package com.coolcoding.rpc.future;

import com.coolcoding.rpc.convert.ConvertUtils;
import com.coolcoding.rpc.model.RpcResponse;

import java.util.concurrent.BlockingQueue;

/**
 * 用于支持异步获取调用结果
 */
public class RpcResponseFuture {
    // 使用ThreadLocal保存当前线程的RpcResponseFuture
    private static final ThreadLocal<RpcResponseFuture> LOCAL = new ThreadLocal<>();

    // 一个队列
    private BlockingQueue<RpcResponse> queue;
    // 方法调用的返回类型
    private Class<?> returyType;

    public void setQueue(BlockingQueue<RpcResponse> queue) {
        this.queue = queue;
    }

    public void setReturyType(Class<?> returyType) {
        this.returyType = returyType;
    }

    public static RpcResponseFuture getRpcResponseFuture() {
        return LOCAL.get();
    }

    // 创建一个RpcResponseFuture并把它设置到ThreadLocal中
    public static RpcResponseFuture newRpcResponseFuture() {
        RpcResponseFuture rpcResponseFuture = new RpcResponseFuture();
        LOCAL.set(rpcResponseFuture);
        return rpcResponseFuture;
    }

    // 获取异步调用的结果
    public Object get() {
        if (queue != null) {
            try {
                RpcResponse rpcResponse = queue.take();
                if (rpcResponse.getCause() != null) {
                    throw rpcResponse.getCause();
                }
                return ConvertUtils.convert(rpcResponse.getResponse(), returyType);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
