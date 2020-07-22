package com.coolcoding.rpc.invoker;

import com.coolcoding.rpc.model.RpcRequest;
import com.coolcoding.rpc.model.RpcResponse;

public interface Invoker {

    RpcResponse invoke(RpcRequest rpcRequest);

}
