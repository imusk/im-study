package com.coolcoding.rpc.client;

import com.coolcoding.rpc.codec.RpcDecoder;
import com.coolcoding.rpc.codec.RpcEncoder;
import com.coolcoding.rpc.model.RpcRequest;
import com.coolcoding.rpc.model.RpcResponse;
import com.coolcoding.rpc.serialize.SerializerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    private Channel channel;

    // 发送rpc request
    public void sendRpcRequest(RpcRequest rpcRequest) throws Exception {
        try {
            // 使用channel直接写数据
            this.channel.writeAndFlush(rpcRequest).sync();
        } catch (Exception e) {
            throw e;
        }
    }

    // 启动客户端，连接服务端
    public void start(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        try {
            // 配置参数
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcEncoder(SerializerFactory.getSerializer()))
                                    .addLast(new RpcDecoder(SerializerFactory.getSerializer(), RpcResponse.class))
                                    .addLast(new NettyClientHandler());
                        }
                    }).option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_KEEPALIVE, true);

            // 客户端连接到服务端
            this.channel = bootstrap.connect(host, port).sync().channel();

            System.out.println("conn to server success...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 这里不能把group关了，关了EventLoop就关了，后面发送请求发不出去了。
        }
    }
}
