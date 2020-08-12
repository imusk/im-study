package com.github.imusk.im.study.protobuf.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @classname: HttpServerChannelHandler
 * @description: HttpServerChannelHandler
 * @data: 2020-07-20 23:26
 * @author: Musk
 */
public class HttpServerChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        ctx.channel().remoteAddress();

        FullHttpRequest request = msg;

        System.out.println("请求方法名称:" + request.method().name());

        System.out.println("uri:" + request.uri());
        ByteBuf buf = request.content();
        System.out.print(buf.toString(CharsetUtil.UTF_8));


        ByteBuf byteBuf = Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

        ctx.writeAndFlush(response);
    }
}