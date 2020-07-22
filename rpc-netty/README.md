# 如何使用Netty打造工业级RPC框架

郑重声明：此框架旨在理解rpc框架的本质，切勿直接在生产环境使用，否则后果自负。

## rpc框架的几点疑问

### 如何通信？
两个服务之间的通信方式有很多，比如http、tcp、websocket、http2.0(stream)，每种方式都有优秀和弊端的地方，比如http是短连接，高并发的时候不合适。

这里我们使用Netty来作为我们的传输层，Netty是对底层io的封装，本身就支持多种协议，扩展性好，性能棒，我们直接使用它的nio传输方式即可。

### 如何像调用本地服务一样无感？
首先，客户端与服务端需要有共同的接口，服务端实现业务逻辑，客户端需要有一个接口的实例，这个实例我们怎么构造呢？其实很简单，通过动态代理即可，动态代理的实现方式有很多，比如jdk proxy，javasist，cglib等，其中jdk proxy方便学习，所以我们这里直接使用jdk proxy作为我们的动态代理部分。

我们通过jdk proxy生成目标接口的一个实例，这实例内部封装接口调用的参数、类型等信息，通过传输层发送到远程服务端，远程服务端通过反射拿到具体的实现类，通过反射调用其对应的方法，拿到结果，再把结果封装通过传输层再传回客户端，客户端拿到结果展示即可。

### 客户端如何动态感知服务端？
通过zookeeper的监听机制，我们可以动态感知一个服务的上线下线，一个客户端可同时监听多个服务端，一个服务端下线了，重试其它服务端，我们今天的框架中先不写注册中心相关的东西。

## 整体流程
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190121111322202.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3Rhbmd0b25nMQ==,size_16,color_FFFFFF,t_70)
通过上图我们可以看到一个rpc框架的全貌，基本包括代理层、协议层、传输层等几个部分，其中协议层又包括编解码、序列化反序列化等部分，代理层又包括客户端的代理和服务端的代理（Invoker），下面我们一一来介绍并编写相关部分的代码。

### 支持层
这一层实际并不存在，是不在上图中的其它的一些对象的统称，你可以在看主体流程的过程中，回来查看相应的数据结构，这部分对象包括：

 - 实际传输的对象，RpcRequest、RpcResponse

RpcRequest.java

```java
package com.coolcoding.rpc.model;

import com.alibaba.fastjson.JSON;

public class RpcRequest {
    // 唯一标识一次请求
    private Long id;
    // 服务名称
    private String serviceName;
    // 方法名称
    private String methodName;
    // 参数
    private Object[] params;
    // 参数类型
    private Class<?>[] paramTypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

```

RpcResponse.java

```java
package com.coolcoding.rpc.model;

import com.alibaba.fastjson.JSON;

public class RpcResponse {
    // 唯一标识一个请求，与RpcRequest中对应
    private Long id;
    // 方法调用的结果
    private Object response;
    // 异常信息
    private Throwable cause;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

```

 - 参数类型的转换，ConvertUtils

ConvertUtils.java

```java
package com.coolcoding.rpc.convert;

/**
 * 转换类型工具类
 * 因为json序列化是不带类型的，生产中建议使用Protobuf等序列化框架，double会被转成BigDecimal
 */
public class ConvertUtils {

    public static Object convert(Object value, Class<?> clz) {
        // 暂时只支持Double，且不支持小写
        if (clz == Double.class) {
            return new Double(value.toString());
        }
        throw new UnsupportedTypeException();
    }
}

```

 - 支持异步调用，RpcResponseFuture

RpcResponseFuture.java

```java
package com.coolcoding.rpc.future;

import com.coolcoding.rpc.convert.ConvertUtils;
import com.coolcoding.rpc.model.RpcResponse;

import java.util.concurrent.BlockingQueue;

/**
 * 用于支持异步获取调用结果
 */
public class RpcResponseFuture {
    // 使用ThreadLocal保存当前线程的RpcResponseFuture
    private static final ThreadLocal<RpcResponseFuture> LOCAL = new ThreadLocal<>();

    // 一个队列
    private BlockingQueue<RpcResponse> queue;
    // 方法调用的返回类型
    private Class<?> returyType;

    public void setQueue(BlockingQueue<RpcResponse> queue) {
        this.queue = queue;
    }

    public void setReturyType(Class<?> returyType) {
        this.returyType = returyType;
    }

    public static RpcResponseFuture getRpcResponseFuture() {
        return LOCAL.get();
    }

    // 创建一个RpcResponseFuture并把它设置到ThreadLocal中
    public static RpcResponseFuture newRpcResponseFuture() {
        RpcResponseFuture rpcResponseFuture = new RpcResponseFuture();
        LOCAL.set(rpcResponseFuture);
        return rpcResponseFuture;
    }

    // 获取异步调用的结果
    public Object get() {
        if (queue != null) {
            try {
                RpcResponse rpcResponse = queue.take();
                if (rpcResponse.getCause() != null) {
                    throw rpcResponse.getCause();
                }
                return ConvertUtils.convert(rpcResponse.getResponse(), returyType);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

```

 - 保存服务

ServiceHolder.java

```java
package com.coolcoding.rpc.invoker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceHolder {

    private static Map<String, Object> map = new ConcurrentHashMap<>();

    public static void publishService(Class<?> serviceClz, Object service) {
        map.put(serviceClz.getName(), service);
    }

    public static Object getService(String serviceName) {
        return map.get(serviceName);
    }
}

```

### 传输层
传输层即客户端与服务端之间的直接通信，使用Netty的话很方便，直接上代码。

 - 客户端代码
 
 代码比较简单，都在注释里面了，这里主要是要把后面发送数据需要用到的channel保存下来。
 
 NettyClient.java
 
 ```java
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

 ```

NettyClientHandle.java

```java
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

```
 
 - 服务端代码

服务端主要是监听端口，设置收到消息的处理器NettyServerHandler。

```java
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

```

NettyServerHandler.java

```java
package com.coolcoding.rpc.server;

import com.coolcoding.rpc.model.RpcRequest;
import com.coolcoding.rpc.invoker.Invoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 服务端收到请求的处理器
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    /**
     * 调用器
     */
    private Invoker invoker;

    public NettyServerHandler(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        // 通过调用器调用到具体的服务进行处理
        ctx.writeAndFlush(invoker.invoke(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}


```

### 协议层
协议层主要是对请求响应的编解码、序列化等操作，当然，像dubbo那种复杂的框架还包含对传输协议等的封装。

#### 编解码器

RpcEncoder.java

```java
package com.coolcoding.rpc.codec;

import com.coolcoding.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {

    private Serializer serializer;

    public RpcEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 序列化对象
        byte[] bytes = serializer.serialize(msg);
        // 写入对象长度（类似请求头）
        out.writeInt(bytes.length);
        // 写入对象字节码（类似请求体）
        out.writeBytes(bytes);
    }
}

```

RpcDecoder.java

```java
package com.coolcoding.rpc.codec;

import com.coolcoding.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码器
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Serializer serializer;
    private Class<?> objectClass;

    public RpcDecoder(Serializer serializer, Class<?> objectClass) {
        this.serializer = serializer;
        this.objectClass = objectClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }

        in.markReaderIndex();
        // 读取请求体长度
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }

        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        // 读取请求体
        byte[] bytes = new byte[dataLength];
        in.readBytes(bytes);

        // 反序列化
        Object obj = serializer.deserialize(bytes, objectClass);
        
        // 交给下一下Handler处理
        out.add(obj);
    }
}

```

#### 序列化
这里主要使用Json序列化，生产环境建议使用protobuf等性能更好的序列化方式。

Serializer.java接口

```java
package com.coolcoding.rpc.serialize;

public interface Serializer {
    byte[] serialize(Object msg);
    <T> T deserialize(byte[] bytes, Class<T> clz);
}

```

JsonSerializer.java

```java
package com.coolcoding.rpc.serialize;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;

/**
 * json序列化
 */
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object msg) {
        // 序列化成字节码
        return JSON.toJSONString(msg).getBytes(Charset.defaultCharset());
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clz) {
        // 反序列化成对象
        return JSON.parseObject(new String(bytes, Charset.defaultCharset()), clz);
    }
}

```


### 代理层
代理层包含客户端代理（proxy）和服务端代理（invoker）。

#### 客户端代理（proxy）
客户端代理，主要封装请求的服务类型、参数、参数类型等信息，这里主要使用jdk自带的代理。

Proxy.java

```java
package com.coolcoding.rpc.proxy;

import com.coolcoding.rpc.client.NettyClient;

/**
 * 代理接口
 */
public interface Proxy {

    /**
     * 创建一个代理类
     * @param interfaceClass 接口类型
     * @param nettyClient 连接哪个服务
     * @param async 是否异步调用
     * @return 返回目标接口的实例
     */
    <T> T newProxy(Class<T> interfaceClass, NettyClient nettyClient, boolean async);
}


```

JdkProxy.java与JdkInvocationHandler.java

```java
package com.coolcoding.rpc.proxy;

import com.coolcoding.rpc.client.NettyClient;
import com.coolcoding.rpc.client.Waiter;
import com.coolcoding.rpc.convert.ConvertUtils;
import com.coolcoding.rpc.future.RpcResponseFuture;
import com.coolcoding.rpc.model.RpcRequest;
import com.coolcoding.rpc.model.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * jdk代理
 */
public class JdkProxy implements Proxy {

    @Override
    public <T> T newProxy(Class<T> interfaceClass, NettyClient nettyClient, boolean async) {
        return (T)java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{interfaceClass}, new JdkInvocationHandler(nettyClient, async));
    }
}

/**
 * jdk代理的处理类
 */
class JdkInvocationHandler implements InvocationHandler {

    private NettyClient nettyClient;
    private boolean async;

    public JdkInvocationHandler(NettyClient nettyClient, boolean async) {
        this.nettyClient = nettyClient;
        this.async = async;
    }

    private static final AtomicLong ATOMIC_LONG = new AtomicLong();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造RpcRequest对象
        RpcRequest rpcRequest = buildRpcRequest(method, args);
        // 发送请求
        return sendRpcRequest(rpcRequest, method.getReturnType());
    }

    private Object sendRpcRequest(RpcRequest rpcRequest, Class<?> returnType) throws Exception {
        // LinkedTransferQueue比Synchronous更优秀，前者offer的时候不会阻塞，后者会阻塞，在EvenLoop线程中更合适
        BlockingQueue<RpcResponse> queue = new LinkedTransferQueue<>();
        // 缓存起来等待返回结果
        Waiter.putQueue(rpcRequest.getId(), queue);

        try {
            // 调用nettyClient发送请求
            nettyClient.sendRpcRequest(rpcRequest);

            // 异步方式
            if (async) {
                // 构造RpcResponseFuture并把队列给它，让它去等待返回结果
                RpcResponseFuture rpcResponseFuture = RpcResponseFuture.newRpcResponseFuture();
                rpcResponseFuture.setQueue(queue);
                rpcResponseFuture.setReturyType(returnType);
                // 直接返回空
                return null;
            }
            // 同步方式这里调用take()方法阻塞等待返回结果
            return ConvertUtils.convert(queue.take().getResponse(), returnType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 构造RpcRequest对象
    private RpcRequest buildRpcRequest(Method method, Object[] args) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setId(ATOMIC_LONG.incrementAndGet());
        rpcRequest.setServiceName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParams(args);

        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        rpcRequest.setParamTypes(paramTypes);

        return rpcRequest;
    }
}

```

#### 服务端代理（invoker）
服务端代理我更愿意把它称作调用器，主要是通过反射找到对应的服务，再调用其对应的方法。

Invoker.java

```java
package com.coolcoding.rpc.invoker;

import com.coolcoding.rpc.model.RpcRequest;
import com.coolcoding.rpc.model.RpcResponse;

public interface Invoker {

    RpcResponse invoke(RpcRequest rpcRequest);

}
```

ReflectInvoker.java

```java
package com.coolcoding.rpc.invoker;

import com.coolcoding.rpc.convert.ConvertUtils;
import com.coolcoding.rpc.model.RpcRequest;
import com.coolcoding.rpc.model.RpcResponse;

import java.lang.reflect.Method;

public class ReflectInvoker implements Invoker {

    @Override
    public RpcResponse invoke(RpcRequest rpcRequest) {
        System.out.println("receive rpc request: " + rpcRequest.toString());

        // 通过服务名称找到相应的服务实例
        Object service = ServiceHolder.getService(rpcRequest.getServiceName());

        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setId(rpcRequest.getId());

        try {
            //  通过方法名称及参数类型找到相应的方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());

            // 参数转一下，主要是类型匹配的问题
            Object[] args = convertArgs(rpcRequest);

            // 调用方法
            Object response = method.invoke(service, args);

            rpcResponse.setResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setCause(e);
        }

        System.out.println("send response: " + rpcResponse.toString());
        return rpcResponse;
    }

    private Object[] convertArgs(RpcRequest rpcRequest) {
        Object[] args = new Object[rpcRequest.getParams().length];
        for (int i=0; i < rpcRequest.getParams().length; i++) {
            args[i] = ConvertUtils.convert(rpcRequest.getParams()[i], rpcRequest.getParamTypes()[i]);
        }
        return args;
    }
}

```

到此为止，整个rpc的框架就差不多了，下面我们来看一个计算器的例子。

## 案例
CalculatorService.java

```java
package com.coolcoding.rpc.example;

public interface CalculatorService {
    Double add(Double n1, Double n2);
    Double sub(Double n1, Double n2);
    Double mul(Double n1, Double n2);
    Double div(Double n1, Double n2);
}

```

CalculatorServiceImpl.java

```java
package com.coolcoding.rpc.example;

public class CalculatorServiceImpl implements CalculatorService {
    @Override
    public Double add(Double n1, Double n2) {
        return n1 + n2;
    }

    @Override
    public Double sub(Double n1, Double n2) {
        return n1 - n2;
    }

    @Override
    public Double mul(Double n1, Double n2) {
        return n1 * n2;
    }

    @Override
    public Double div(Double n1, Double n2) {
        return n1 / n2;
    }
}

```

CalculatorServer.java

```java
package com.coolcoding.rpc.example;

import com.coolcoding.rpc.invoker.ServiceHolder;
import com.coolcoding.rpc.server.NettyServer;

public class CalculatorServer {
    public static void main(String[] args) {
        // 发布一个服务
        ServiceHolder.publishService(CalculatorService.class, new CalculatorServiceImpl());

        // 调动服务端
        new NettyServer(8080).start();
    }
}

```

CalculatorClient.java

```java
package com.coolcoding.rpc.example;

import com.coolcoding.rpc.client.NettyClient;
import com.coolcoding.rpc.future.RpcResponseFuture;
import com.coolcoding.rpc.proxy.Proxy;
import com.coolcoding.rpc.proxy.ProxyFactory;

public class CalculatorClient {

    public static void main(String[] args) throws Exception {

        // 启动客户端
        NettyClient nettyClient = new NettyClient();
        nettyClient.start("localhost", 8080);

        Proxy proxy = ProxyFactory.getProxy();

        // 同步调用
        CalculatorService calculatorService = proxy.newProxy(CalculatorService.class, nettyClient, false);
        System.out.println("sync: " + calculatorService.add(1D, 2D));

        // 同步调用
        System.out.println("sync: " + calculatorService.sub(1D, 2D));

        // 异步调用
        calculatorService = proxy.newProxy(CalculatorService.class, nettyClient, true);
        System.out.println("async1: " + calculatorService.mul(22.8D, 30.5D));
        RpcResponseFuture rpcResponseFuture = RpcResponseFuture.getRpcResponseFuture();
        System.out.println("async1: " + rpcResponseFuture.get());

        // 异步调用
        System.out.println("async1: " + calculatorService.div(22.8D, 30.5D));
        rpcResponseFuture = RpcResponseFuture.getRpcResponseFuture();
        System.out.println("async1: " + rpcResponseFuture.get());

    }
}

```

依次启动服务端和客户端，可看到以下打印结果，说明我们的rpc框架可以正常使用。

```
conn to server success...
sync: 3.0
sync: -1.0
async1: null
async1: 695.4
async1: null
async1: 0.7475409836065574
```

## 源码下载

https://github.com/alan-tang-tt/rpc

