package com.wen.im.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.UUID;

/**
 * @author 93806-wenzhou
 * @date 2020-07-20 16:26
 * @email iwenzhou@qq.com
 * @description 基于Netty实现的服务端
 * https://www.jianshu.com/p/ec3ebb396943
 */
public class NettyServer {

    public static void aa(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        // boos对应，IOServer.java中的接受新连接线程，主要负责创建新连接
        NioEventLoopGroup boos = new NioEventLoopGroup();

        // worker对应 IOClient.java中的负责读取数据的线程，主要用于读取数据以及业务逻辑处理
        NioEventLoopGroup worker = new NioEventLoopGroup();

        serverBootstrap
                // 配置线程模型(两大线程)
                .group(boos, worker)
                // 置顶IO模型为NIO
                .channel(NioServerSocketChannel.class)
                // 连接读写处理逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                // 绑定端口
                .bind(8000)
        ;

    }

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        // boos对应，IOServer.java中的接受新连接线程，主要负责创建新连接
        NioEventLoopGroup boos = new NioEventLoopGroup();

        // worker对应 IOClient.java中的负责读取数据的线程，主要用于读取数据以及业务逻辑处理
        NioEventLoopGroup worker = new NioEventLoopGroup();

        serverBootstrap
                // 配置线程模型(两大线程)
                .group(boos, worker)
                // 置顶IO模型为NIO
                .channel(NioServerSocketChannel.class)
                // 连接读写处理逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                // 指定在服务端启动过程中的一些逻辑，通常情况下呢，我们用不着这个方法
                .handler(new ChannelInitializer<NioServerSocketChannel>() {
                    @Override
                    protected void initChannel(NioServerSocketChannel ch) {
                        System.out.println("服务端启动中");
                    }
                })
                // 给服务端的channel，也就是NioServerSocketChannel指定一些自定义属性，然后我们可以通过channel.attr()取出这个属性
                .attr(AttributeKey.newInstance("serverName"), "nettyServer")
                // 给每一条连接指定自定义属性
                .childAttr(AttributeKey.newInstance("clientKey"), "clientId=" + UUID.randomUUID())
                // 开启TCP底层心跳机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 开始Nagle算法，true表示关闭，false表示开启，通俗地说，如果要求高实时性，有数据发送时就马上发送，就关闭，如果需要减少发送次数减少网络交互，就开启。
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 绑定端口
                .bind(8000)
                // 监听端口绑定是否成功(可选)
                .addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (future.isSuccess()) {
                            System.out.println("端口绑定成功!");
                        } else {
                            System.err.println("端口绑定失败!");
                        }
                    }
                })

        ;

    }

    /**
     * 自动绑定递增端口
     * @param serverBootstrap
     * @param port
     */
    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) {
                if (future.isSuccess()) {
                    System.out.println("端口[" + port + "]绑定成功!");
                } else {
                    System.err.println("端口[" + port + "]绑定失败!");
                    bind(serverBootstrap, port + 1);
                }
            }
        });
    }

}
