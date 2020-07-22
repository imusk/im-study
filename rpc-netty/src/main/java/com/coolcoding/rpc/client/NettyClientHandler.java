package com.coolcoding.rpc.client;

import com.coolcoding.rpc.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端收到响应的处理器
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        // 取得发送数据时的绑定的队列，并把返回值放入进去
        Waiter.getQueue(msg.getId()).offer(msg);
        Waiter.removeQueue(msg.getId());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
