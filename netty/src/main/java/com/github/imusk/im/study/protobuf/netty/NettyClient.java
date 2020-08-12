package com.github.imusk.im.study.protobuf.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Date;

/**
 * @author 93806-wenzhou
 * @date 2020-07-20 16:32
 * @email iwenzhou@qq.com
 * @description 基于Netty实现的客户端
 */
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });

        Channel channel = bootstrap.connect("127.0.0.1", 8000).channel();

        AttributeKey<Object> serverNameKey = AttributeKey.newInstance("serverName");
        AttributeKey<Object> clientKey = AttributeKey.newInstance("clientKey");

        while (true) {
            channel.writeAndFlush(new Date() + ": hello world!");

            // 取出属性
            if (channel.hasAttr(serverNameKey)) {
                Attribute<Object> serverName = channel.attr(serverNameKey);
                System.out.println("serverName = " + serverName.get());
            }

            if (channel.hasAttr(clientKey)) {
                Attribute<Object> clientId = channel.attr(clientKey);
                System.out.println("clientId = " + clientId.get());
            }

            Thread.sleep(2000);
        }
    }

}
