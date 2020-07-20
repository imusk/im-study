package com.wen.im.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @classname: HttpServer
 * @description: 使用Netty编写一个Http服务器程序
 * @data: 2020-07-20 23:23
 * @author: Musk
 * https://mp.weixin.qq.com/s/-BKHr8pcXGrf1eZV9Ht71w
 */
public class HttpServer {

    public static void main(String[] args) {
        //构造两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //服务端启动辅助类
            ServerBootstrap bootstrap = new ServerBootstrap();


            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());


            ChannelFuture future = bootstrap.bind(8080).sync();
            //等待服务端口关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
