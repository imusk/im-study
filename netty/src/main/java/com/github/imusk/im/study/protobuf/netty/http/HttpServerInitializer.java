package com.github.imusk.im.study.protobuf.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @classname: HttpServerInitializer
 * @description: HttpServerInitializer
 * @data: 2020-07-20 23:24
 * @author: Musk
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        // 通过SocketChannel去获得对应的管道
        ChannelPipeline pipeline = sc.pipeline();

        //处理http消息的编解码
        // 通过管道，添加handler
        // HttpServerCodec是由netty自己提供的助手类，可以理解为拦截器
        // 当请求到服务端，我们需要做解码，响应到客户端做编码
        pipeline.addLast("httpServerCodec", new HttpServerCodec());

        //添加自定义的ChannelHandler
        pipeline.addLast("httpServerHandler", new HttpServerHandler());
    }


}