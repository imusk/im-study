package com.coolcoding.rpc.invoker;

public class InvokerFactory {

    public static Invoker getInvoker(){
        return new ReflectInvoker();
    }
}
