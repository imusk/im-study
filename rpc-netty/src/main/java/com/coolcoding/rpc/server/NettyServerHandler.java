package com.coolcoding.rpc.server;

import com.coolcoding.rpc.model.RpcRequest;
import com.coolcoding.rpc.invoker.Invoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 服务端收到请求的处理器
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    /**
     * 调用器
     */
    private Invoker invoker;

    public NettyServerHandler(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        // 通过调用器调用到具体的服务进行处理
        ctx.writeAndFlush(invoker.invoke(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
