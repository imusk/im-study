package com.github.imusk.im.study.protobuf.netty.ssl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author 93806-wenzhou
 * @date 2020-07-20 16:32
 * @email iwenzhou@qq.com
 * @description 基于Netty实现的客户端
 */
public class NettySSLClient {

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        String clientPath = System.getProperty("user.dir") + "/netty/src/main/resources/certs/clientCerts.jks";
                        String keyStorePass = "123456";

                        //客户方模式
                        SSLContext sslContext = MyClientSslContextFactory.getClientContext(clientPath, keyStorePass);
                        SSLEngine sslEngine = sslContext.createSSLEngine();
                        sslEngine.setUseClientMode(true);
                        ch.pipeline().addFirst("ssl", new SslHandler(sslEngine));

                        IdleStateHandler idleStateHandler = new IdleStateHandler(1, 1, 1, TimeUnit.SECONDS);
                        ch.pipeline().addLast("idleCheck", idleStateHandler);
                        ch.pipeline().addLast("heartbeat", new HeartBeatHandler());
                        ch.pipeline().addLast(new StringEncoder());

                    }
                });

        Channel channel = bootstrap.connect("127.0.0.1", 18080).channel();

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

class ClientSslContextFactory {

    private static final String PROTOCOL = "TLS";

    private static SSLContext CLIENT_CONTEXT;

    static SSLContext getClientContext(String caPath, String password) {
        if (CLIENT_CONTEXT != null) {
            return CLIENT_CONTEXT;
        }
        InputStream inputStream = null;
        try {
            //信任库
            TrustManagerFactory trustManagerFactory = null;
            if (caPath != null) {
                //密钥库KeyStore
                KeyStore tks = KeyStore.getInstance("JKS");
                //加载客户端证书
                inputStream = new FileInputStream(caPath);
                tks.load(inputStream, password.toCharArray());
                trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
                // 初始化信任库
                trustManagerFactory.init(tks);
            }
            CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
            //设置信任证书
            TrustManager[] trustManagers = trustManagerFactory == null ? null : trustManagerFactory.getTrustManagers();
            CLIENT_CONTEXT.init(null, trustManagers, null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return CLIENT_CONTEXT;
    }

}

/**
 * 心跳发送[0000]
 */
class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                ctx.channel().writeAndFlush(new Random().nextInt(100));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
