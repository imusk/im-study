package com.github.imusk.im.study.redis.utils;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.codec.Utf8StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.masterslave.MasterSlave;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: Musk
 * @data: 2020-07-27 00:24
 * @classname: LettuceRedisUtilTest
 * @description: https://www.cnblogs.com/throwable/p/11601538.html
 */
class LettuceRedisUtilTest {

    private static final Logger log = LoggerFactory.getLogger(LoggerFactory.class);

    private static RedisClient CLIENT;

    private static StatefulRedisConnection<String, String> CONNECTION;

    private static RedisCommands<String, String> COMMAND;

    private static RedisAsyncCommands<String, String> ASYNC_COMMAND;

    private static RedisReactiveCommands<String, String> REACTIVE_COMMAND;

    @BeforeEach
    public void beforeClass() {
        RedisURI redisUri = RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        CLIENT = RedisClient.create(redisUri);
        CONNECTION = CLIENT.connect();

        // 同步API
        COMMAND = CONNECTION.sync();

        // 异步API
        ASYNC_COMMAND = CONNECTION.async();

        // 反应式API
        REACTIVE_COMMAND = CONNECTION.reactive();
    }

    @AfterEach
    public void afterClass() throws Exception {
        CONNECTION.close();
        CLIENT.shutdown();
    }

    /**
     * 简单的示例
     * @throws Exception
     */
    @Test
    public void testSetGet() throws Exception {
        // <1> 创建单机连接的连接信息
        RedisURI redisUri = RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();

        // <2> 创建客户端
        RedisClient redisClient = RedisClient.create(redisUri);
        // <3> 创建线程安全的连接
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        // <4> 创建同步命令
        RedisCommands<String, String> redisCommands = connection.sync();
        // 设置过期时间：5秒
        SetArgs setArgs = SetArgs.Builder.nx().ex(5);
        String result = redisCommands.set("name", "throwable", setArgs);
        Assertions.assertThat(result).isEqualToIgnoringCase("OK");
        result = redisCommands.get("name");
        Assertions.assertThat(result).isEqualTo("throwable");
        // ... 其他操作

        // <5> 关闭连接
        connection.close();
        // <6> 关闭客户端
        redisClient.shutdown();
    }

    // ------ 同步 API ------

    @Test
    public void testSyncPing() throws Exception {
        String pong = COMMAND.ping();
        Assertions.assertThat(pong).isEqualToIgnoringCase("PONG");
    }


    @Test
    public void testSyncSetAndGet() throws Exception {
        SetArgs setArgs = SetArgs.Builder.nx().ex(5);
        COMMAND.set("name", "throwable", setArgs);
        String value = COMMAND.get("name");
        log.info("Get value: {}", value);
    }

    // ------ 异步 API ------

    @Test
    public void testAsyncPing() throws Exception {
        RedisFuture<String> redisFuture = ASYNC_COMMAND.ping();
        log.info("Ping result:{}", redisFuture.get());
    }

    @Test
    public void testAsyncSetAndGet1() throws Exception {
        SetArgs setArgs = SetArgs.Builder.nx().ex(5);
        RedisFuture<String> future = ASYNC_COMMAND.set("name", "throwable", setArgs);
        // CompletableFuture#thenAccept()
        future.thenAccept(value -> log.info("Set命令返回:{}", value));
        // Set命令返回:OK
        // Future#get()
        String futureResult = future.get();
        System.out.println(futureResult);

        RedisFuture<String> redisFuture = ASYNC_COMMAND.get("name");
        String result = redisFuture.get();
        System.out.println(result);
    }

    @Test
    public void testAsyncSetAndGet2() throws Exception {
        SetArgs setArgs = SetArgs.Builder.nx().ex(5);
        CompletableFuture<Void> result =
                (CompletableFuture<Void>) ASYNC_COMMAND.set("name", "throwable", setArgs)
                        .thenAcceptBoth(ASYNC_COMMAND.get("name"),
                                (s, g) -> {
                                    log.info("Set命令返回:{}", s);
                                    log.info("Get命令返回:{}", g);
                                });
        result.get();

        // Set命令返回:OK
        // Get命令返回:throwable
    }

    // ------ 反应式 API ------

    @Test
    public void testReactivePing() throws Exception {
        Mono<String> ping = REACTIVE_COMMAND.ping();
        ping.subscribe(v -> log.info("Ping result:{}", v));
        Thread.sleep(1000);
    }

    @Test
    public void testReactiveSetAndGet() throws Exception {
        SetArgs setArgs = SetArgs.Builder.nx().ex(5);
        REACTIVE_COMMAND.set("name", "throwable", setArgs).block();
        REACTIVE_COMMAND.get("name").subscribe(value -> log.info("Get命令返回:{}", value));
        Thread.sleep(1000);
        // Get命令返回:throwable
    }

    @Test
    public void testReactiveSet() throws Exception {
        REACTIVE_COMMAND.sadd("food", "bread", "meat", "fish").block();
        Flux<String> flux = REACTIVE_COMMAND.smembers("food");
        flux.subscribe(log::info);
        REACTIVE_COMMAND.srem("food", "bread", "meat", "fish").block();
        Thread.sleep(1000);
        // meat
        // bread
        // fish
    }

    @Test
    public void testReactiveFunctional() throws Exception {
        REACTIVE_COMMAND.multi().doOnSuccess(r -> {
            REACTIVE_COMMAND.set("counter", "1").doOnNext(log::info).subscribe();
            REACTIVE_COMMAND.incr("counter").doOnNext(c -> log.info(String.valueOf(c))).subscribe();
        }).flatMap(s -> REACTIVE_COMMAND.exec())
                .doOnNext(transactionResult -> log.info("Discarded:{}", transactionResult.wasDiscarded()))
                .subscribe();
        Thread.sleep(1000);
        // OK
        // 2
        // Discarded:false
    }


    /**
     * 这里用单机同步命令的模式举一个Redis键空间通知
     * @throws Exception
     */
    @Test
    public void testSyncKeyspaceNotification() throws Exception {
        RedisURI redisUri = RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                // 注意这里只能是0号库
                .withDatabase(0)
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();

        RedisClient redisClient = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> redisConnection = redisClient.connect();

        RedisCommands<String, String> redisCommands = redisConnection.sync();

        // 只接收键过期的事件
        redisCommands.configSet("notify-keyspace-events", "Ex");

        StatefulRedisPubSubConnection<String, String> connection = redisClient.connectPubSub();
        connection.addListener(new RedisPubSubAdapter<String, String>() {
            @Override
            public void psubscribed(String pattern, long count) {
                log.info("pattern:{},count:{}", pattern, count);
            }

            @Override
            public void message(String pattern, String channel, String message) {
                log.info("pattern:{},channel:{},message:{}", pattern, channel, message);
            }
        });

        RedisPubSubCommands<String, String> commands = connection.sync();
        commands.psubscribe("__keyevent@0__:expired");
        redisCommands.setex("name", 2, "throwable");

        Thread.sleep(10000);

        redisConnection.close();
        connection.close();
        redisClient.shutdown();

        // pattern:__keyevent@0__:expired,count:1
        // pattern:__keyevent@0__:expired,channel:__keyevent@0__:expired,message:name
    }


    // ------ 事务和批量命令执行 ------

    /**
     * 同步批量执行命令模式
     * @throws Exception
     * @description
     * Redis的Pipeline也就是管道机制可以理解为把多个命令打包在一次请求发送到Redis服务端，然后Redis服务端把所有的响应结果打包好一次性返回，从而节省不必要的网络资源（最主要是减少网络请求次数）。Redis对于Pipeline机制如何实现并没有明确的规定，也没有提供特殊的命令支持Pipeline机制。Jedis中底层采用BIO（阻塞IO）通讯，所以它的做法是客户端缓存将要发送的命令，最后需要触发然后同步发送一个巨大的命令列表包，再接收和解析一个巨大的响应列表包。Pipeline在Lettuce中对使用者是透明的，由于底层的通讯框架是Netty，所以网络通讯层面的优化Lettuce不需要过多干预，换言之可以这样理解：Netty帮Lettuce从底层实现了Redis的Pipeline机制。
     */
    @Test
    public void testSyncMulti() throws Exception {
        COMMAND.multi();
        COMMAND.setex("name-1", 2, "throwable");
        COMMAND.setex("name-2", 2, "doge");
        TransactionResult result = COMMAND.exec();
        int index = 0;
        for (Object r : result) {
            log.info("Result-{}:{}", index, r);
            index++;
        }

        // Result-0:OK
        // Result-1:OK
    }

    /**
     * 异步 手动Flush
     */
    @Test
    public void testAsyncManualFlush() {
        // 取消自动flush
        ASYNC_COMMAND.setAutoFlushCommands(false);
        List<RedisFuture<?>> redisFutures = Lists.newArrayList();
        int count = 5000;
        for (int i = 0; i < count; i++) {
            String key = "key-" + (i + 1);
            String value = "value-" + (i + 1);
            redisFutures.add(ASYNC_COMMAND.set(key, value));
            redisFutures.add(ASYNC_COMMAND.expire(key, 2));
        }
        long start = System.currentTimeMillis();
        ASYNC_COMMAND.flushCommands();
        boolean result = LettuceFutures.awaitAll(10, TimeUnit.SECONDS, redisFutures.toArray(new RedisFuture[0]));
        Assertions.assertThat(result).isTrue();
        log.info("Lettuce cost:{} ms", System.currentTimeMillis() - start);

        // Lettuce cost:1302 ms
    }

    @Test
    public void testJedisPipeline() throws Exception {
        Jedis jedis = new Jedis();
        Pipeline pipeline = jedis.pipelined();
        int count = 5000;
        for (int i = 0; i < count; i++) {
            String key = "key-" + (i + 1);
            String value = "value-" + (i + 1);
            pipeline.set(key, value);
            pipeline.expire(key, 2);
        }
        long start = System.currentTimeMillis();
        pipeline.syncAndReturnAll();
        log.info("Jedis cost:{} ms", System.currentTimeMillis()  - start);
        // Jedis cost:9 ms
    }


    // ------ 主从模式 ------

    /**
     * 假设现在有三个Redis服务形成树状主从关系如下：
     *
     * 节点一：localhost:6379，角色为Master。
     * 节点二：localhost:6380，角色为Slavor，节点一的从节点。
     * 节点三：localhost:6381，角色为Slavor，节点二的从节点。
     * 首次动态节点发现主从模式的节点信息需要如下构建连接：
     *
     * @throws Exception
     */
    @Test
    public void testDynamicReplica() throws Exception {
        // 这里只需要配置一个节点的连接信息，不一定需要是主节点的信息，从节点也可以
        RedisURI uri = RedisURI.builder().withHost("localhost").withPort(6379).build();
        RedisClient redisClient = RedisClient.create(uri);
        StatefulRedisMasterReplicaConnection<String, String> connection = MasterReplica.connect(redisClient, StringCodec.UTF8, uri);
        // 只从从节点读取数据
        connection.setReadFrom(ReadFrom.REPLICA);
        //connection.setReadFrom(ReadFrom.SLAVE);
        // 执行其他Redis命令
        connection.close();
        redisClient.shutdown();
    }

    /**
     * 如果需要指定静态的Redis主从节点连接属性，那么可以这样构建连接：
     * @throws Exception
     */
    @Test
    public void testStaticReplica() throws Exception {
        List<RedisURI> uris = new ArrayList<>();
        RedisURI uri1 = RedisURI.builder().withHost("localhost").withPort(6379).build();
        RedisURI uri2 = RedisURI.builder().withHost("localhost").withPort(6380).build();
        RedisURI uri3 = RedisURI.builder().withHost("localhost").withPort(6381).build();
        uris.add(uri1);
        uris.add(uri2);
        uris.add(uri3);
        RedisClient redisClient = RedisClient.create();
        StatefulRedisMasterReplicaConnection<String, String> connection = MasterReplica.connect(redisClient, StringCodec.UTF8, uris);
        // 只从主节点读取数据
        connection.setReadFrom(ReadFrom.MASTER);
        // 执行其他Redis命令
        connection.close();
        redisClient.shutdown();
    }

    // ------ 哨兵模式 ------

    @Test
    public void testDynamicSentinel() throws Exception {
        RedisURI redisUri = RedisURI.builder()
                .withPassword("你的密码")
                .withSentinel("localhost", 26379)
                .withSentinelMasterId("哨兵Master的ID")
                .build();
        RedisClient redisClient = RedisClient.create();
        StatefulRedisMasterReplicaConnection<String, String> connection = MasterReplica.connect(redisClient, StringCodec.UTF8, redisUri);
        // 只允许从从节点读取数据
        connection.setReadFrom(ReadFrom.SLAVE);
        RedisCommands<String, String> command = connection.sync();
        SetArgs setArgs = SetArgs.Builder.nx().ex(5);
        command.set("name", "throwable", setArgs);
        String value = command.get("name");
        log.info("Get value:{}", value);
        // Get value:throwable
    }

}