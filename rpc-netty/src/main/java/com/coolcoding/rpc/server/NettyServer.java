package com.coolcoding.rpc.server;


import com.coolcoding.rpc.codec.RpcDecoder;
import com.coolcoding.rpc.codec.RpcEncoder;
import com.coolcoding.rpc.invoker.InvokerFactory;
import com.coolcoding.rpc.model.RpcRequest;
import com.coolcoding.rpc.serialize.SerializerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer   {

    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    // 启动服务端
    public void start() {
        // 接受连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 处理业务
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcDecoder(SerializerFactory.getSerializer(), RpcRequest.class))
                                    .addLast(new RpcEncoder(SerializerFactory.getSerializer()))
                                    .addLast(new NettyServerHandler(InvokerFactory.getInvoker()));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口，启动监听
            ChannelFuture future = bootstrap.bind(port).sync();

            System.out.println("server started...");

            // 这里会阻塞
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

}
