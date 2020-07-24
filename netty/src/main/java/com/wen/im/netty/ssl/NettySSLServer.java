package com.wen.im.netty.ssl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 93806-wenzhou
 * @date 2020-07-20 16:26
 * @email iwenzhou@qq.com
 * @description 基于Netty实现的服务端
 * https://www.jianshu.com/p/ec3ebb396943
 */
public class NettySSLServer {

    public static void main(String[] args) {

        // boos对应，IOServer.java中的接受新连接线程，主要负责创建新连接
        NioEventLoopGroup boss = new NioEventLoopGroup();

        // worker对应 IOClient.java中的负责读取数据的线程，主要用于读取数据以及业务逻辑处理
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap
                    // 配置线程模型(两大线程)
                    .group(boss, worker)
                    // 置顶IO模型为NIO
                    .channel(NioServerSocketChannel.class)
                    // 连接读写处理逻辑
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 添加netty自带的sslHandler
                            String jksPath = System.getProperty("user.dir") + "/netty/src/main/resources/certs/serverCerts.jks";
                            String keyStorePass = "123456";
                            String keyPassword = "123456";
                            SSLContext sslContext = MyServerSslContextFactory.getServerContext(jksPath, keyStorePass, keyPassword);
                            //设置为服务器模式
                            SSLEngine sslEngine = sslContext.createSSLEngine();
                            sslEngine.setUseClientMode(false);
                            //是否需要验证客户端 。 如果是双向认证，则需要将其设置为true，同时将client证书添加到server的信任列表中
                            sslEngine.setNeedClientAuth(false);
                            pipeline.addFirst("ssl", new SslHandler(sslEngine));   //这个handler需要加到最前面

                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                    System.out.println(msg);
                                }
                            });

                            // 添加心跳支持
                            pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            // 基于定长的方式解决粘包/拆包问题
                            // pipeline.addLast(new LengthFieldBasedFrameDecoder(nettyConfig.getMaxFrameLength(), 0, 2, 0, 2));
                            // pipeline.addLast(new LengthFieldPrepender(2));
                            // 序列化
                            // pipeline.addLast(new MessagePackDecoder());
                            // pipeline.addLast(new MessagePackEncoder());

                        }
                    })
            ;

            System.out.println("netty服务器在[18080]端口启动监听");
            ChannelFuture f = serverBootstrap.bind(18080).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("[出现异常] 释放资源");
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
