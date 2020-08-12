package com.github.imusk.im.study.redis.utils;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @classname: LettuceRedisUtil
 * @description: LettuceRedisUtil
 * @data: 2020-07-26 21:13
 * @author: Musk
 */
public class LettuceRedisUtil {

    private static RedisAsyncCommands<String, String> lettuce;
    private static final String REDIS_HOST = "127.0.0.1";
    private static final int REDIS_PORT = 6379;
    private static final int REDIS_EXPIRE = 1;
    private static final int REDIS_MAX_IDLE = 10;
    private static final int REDIS_MAX_ACTIVE = 10;

    static {
        RedisURI redisURI = new RedisURI();
        redisURI.setHost(REDIS_HOST);
        redisURI.setPort(REDIS_PORT);
        // redisURI.setPassword("");
        redisURI.setTimeout(Duration.ofSeconds(100));
//        redisURI.setUnit(TimeUnit.SECONDS);
        // 也可直接将url的字符串传入 RedisClient.create()方法中 eg:redis://[password@]host[:port][/databaseNumber]
        RedisClient client = RedisClient.create(redisURI); //
        // 从redis客户端中获取一个异步的redis缓冲池
        lettuce = client.connect().async();
//        pool = client.asyncPool(REDIS_MAX_IDLE, REDIS_MAX_ACTIVE);
        // 参数说明：REDIS_MAX_IDLE 为本缓冲池中最大闲置连接数量 // REDIS_MAX_ACTIVE为本缓冲池中最大活动连接数量
    }



    // 关闭服务器时 关闭缓冲池
    public static void shutDown() {
        lettuce.shutdown(true);
    }

    public static String getString(String key) {
        RedisFuture<String> redisFuture = lettuce.get(key);
        try {
            return redisFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object setString(String key, String value) {
        RedisFuture<String> redisFuture = lettuce.set(key, value);
        try {
            return redisFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void cluster() {
        List<RedisURI> list = new ArrayList<>();
        list.add(RedisURI.create("redis://192.168.2.4:7000"));
        list.add(RedisURI.create("redis://192.168.2.5:7000"));
        list.add(RedisURI.create("redis://192.168.2.6:7000"));
        list.add(RedisURI.create("redis://192.168.2.4:7001"));
        list.add(RedisURI.create("redis://192.168.2.5:7001"));
        list.add(RedisURI.create("redis://192.168.2.6:7001"));
        RedisClusterClient clusterClient = RedisClusterClient.create(list);
        //集群Redis
        RedisClusterClient client = RedisClusterClient.create(list);
        GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool;
        GenericObjectPoolConfig<StatefulRedisClusterConnection<String, String>> poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMinIdle(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMaxTotal(16);
        poolConfig.setMinEvictableIdleTimeMillis(1000 * 30);
        poolConfig.setSoftMinEvictableIdleTimeMillis(1000 * 30);
        poolConfig.setMaxWaitMillis(0);
        pool = ConnectionPoolSupport.createGenericObjectPool(() -> {
            System.err.println("Requesting new StatefulRedisClusterConnection " + System.currentTimeMillis());
            return client.connect();
        }, poolConfig);

        StatefulRedisClusterConnection<String, String> connection = null;
        try {
            connection = pool.borrowObject();
            connection.setReadFrom(ReadFrom.MASTER_PREFERRED);

            RedisAdvancedClusterAsyncCommands<String, String> commands = connection.async();
            commands.set("id", "taozhongyu");
            RedisFuture<String> future = commands.get("id");
            String str = future.get();
            System.out.println(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        pool.close();
        clusterClient.shutdown();

    }

}
