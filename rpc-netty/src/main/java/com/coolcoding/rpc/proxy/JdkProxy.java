package com.coolcoding.rpc.proxy;

import com.coolcoding.rpc.client.NettyClient;
import com.coolcoding.rpc.client.Waiter;
import com.coolcoding.rpc.convert.ConvertUtils;
import com.coolcoding.rpc.future.RpcResponseFuture;
import com.coolcoding.rpc.model.RpcRequest;
import com.coolcoding.rpc.model.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * jdk代理
 */
public class JdkProxy implements Proxy {

    @Override
    public <T> T newProxy(Class<T> interfaceClass, NettyClient nettyClient, boolean async) {
        return (T)java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{interfaceClass}, new JdkInvocationHandler(nettyClient, async));
    }
}

/**
 * jdk代理的处理类
 */
class JdkInvocationHandler implements InvocationHandler {

    private NettyClient nettyClient;
    private boolean async;

    public JdkInvocationHandler(NettyClient nettyClient, boolean async) {
        this.nettyClient = nettyClient;
        this.async = async;
    }

    private static final AtomicLong ATOMIC_LONG = new AtomicLong();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造RpcRequest对象
        RpcRequest rpcRequest = buildRpcRequest(method, args);
        // 发送请求
        return sendRpcRequest(rpcRequest, method.getReturnType());
    }

    private Object sendRpcRequest(RpcRequest rpcRequest, Class<?> returnType) throws Exception {
        // LinkedTransferQueue比Synchronous更优秀，前者offer的时候不会阻塞，后者会阻塞，在EvenLoop线程中更合适
        BlockingQueue<RpcResponse> queue = new LinkedTransferQueue<>();
        // 缓存起来等待返回结果
        Waiter.putQueue(rpcRequest.getId(), queue);

        try {
            // 调用nettyClient发送请求
            nettyClient.sendRpcRequest(rpcRequest);

            // 异步方式
            if (async) {
                // 构造RpcResponseFuture并把队列给它，让它去等待返回结果
                RpcResponseFuture rpcResponseFuture = RpcResponseFuture.newRpcResponseFuture();
                rpcResponseFuture.setQueue(queue);
                rpcResponseFuture.setReturyType(returnType);
                // 直接返回空
                return null;
            }
            // 同步方式这里调用take()方法阻塞等待返回结果
            return ConvertUtils.convert(queue.take().getResponse(), returnType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 构造RpcRequest对象
    private RpcRequest buildRpcRequest(Method method, Object[] args) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setId(ATOMIC_LONG.incrementAndGet());
        rpcRequest.setServiceName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParams(args);

        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        rpcRequest.setParamTypes(paramTypes);

        return rpcRequest;
    }
}
