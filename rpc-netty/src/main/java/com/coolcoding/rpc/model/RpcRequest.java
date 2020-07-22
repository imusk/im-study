package com.coolcoding.rpc.model;

import com.alibaba.fastjson.JSON;

public class RpcRequest {
    // 唯一标识一次请求
    private Long id;
    // 服务名称
    private String serviceName;
    // 方法名称
    private String methodName;
    // 参数
    private Object[] params;
    // 参数类型
    private Class<?>[] paramTypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
