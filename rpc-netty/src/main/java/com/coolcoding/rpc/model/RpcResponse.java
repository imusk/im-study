package com.coolcoding.rpc.model;

import com.alibaba.fastjson.JSON;

public class RpcResponse {
    // 唯一标识一个请求，与RpcRequest中对应
    private Long id;
    // 方法调用的结果
    private Object response;
    // 异常信息
    private Throwable cause;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
