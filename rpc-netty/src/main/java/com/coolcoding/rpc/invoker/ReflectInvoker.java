package com.coolcoding.rpc.invoker;

import com.coolcoding.rpc.convert.ConvertUtils;
import com.coolcoding.rpc.model.RpcRequest;
import com.coolcoding.rpc.model.RpcResponse;

import java.lang.reflect.Method;

public class ReflectInvoker implements Invoker {

    @Override
    public RpcResponse invoke(RpcRequest rpcRequest) {
        System.out.println("receive rpc request: " + rpcRequest.toString());

        // 通过服务名称找到相应的服务实例
        Object service = ServiceHolder.getService(rpcRequest.getServiceName());

        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setId(rpcRequest.getId());

        try {
            //  通过方法名称及参数类型找到相应的方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());

            // 参数转一下，主要是类型匹配的问题
            Object[] args = convertArgs(rpcRequest);

            // 调用方法
            Object response = method.invoke(service, args);

            rpcResponse.setResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setCause(e);
        }

        System.out.println("send response: " + rpcResponse.toString());
        return rpcResponse;
    }

    private Object[] convertArgs(RpcRequest rpcRequest) {
        Object[] args = new Object[rpcRequest.getParams().length];
        for (int i=0; i < rpcRequest.getParams().length; i++) {
            args[i] = ConvertUtils.convert(rpcRequest.getParams()[i], rpcRequest.getParamTypes()[i]);
        }
        return args;
    }
}
