package com.wen.im.hazelcast.config;

import com.hazelcast.config.*;
import com.hazelcast.config.cp.FencedLockConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author: Musk
 * @data: 2020-07-29 19:04
 * @classname: HazelcastConfiguration
 * @description: HazelcastConfiguration
 */
@Configuration
public class HazelcastConfiguration {

    public static final String ID_GENERATOR = "idGenerator";

    /**
     * 本地启动嵌入式hazelcast集群配置，会在本地启动hazelcast服务器并组好集群
     * @return
     */
    @Bean
    public Config hazelCastConfig() {
        // 设置集群管理中心
        ManagementCenterConfig centerConfig = new ManagementCenterConfig();
        centerConfig.setUrl("http://localhost:8080/hazelcast-mancenter");
        centerConfig.setEnabled(true);

        FencedLockConfig fencedLockConfig = new FencedLockConfig();
        // 不可重入
        fencedLockConfig.disableReentrancy();

        Config config = new Config();
        //解决同网段下，不同库项目
        GroupConfig gc=new GroupConfig("hazelGroup");
        config.setInstanceName("hazelcast-instance")
                .addMapConfig(new MapConfig()
                        .setName("configuration")
                        // Map中存储条目的最大值[0~Integer.MAX_VALUE]。默认值为0。
                        .setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                        //数据释放策略[NONE|LRU|LFU]。这是Map作为缓存的一个参数，用于指定数据的回收算法。默认为NONE。LRU：“最近最少使用“策略。
                        .setEvictionPolicy(EvictionPolicy.LRU)
                        //数据留存时间[0~Integer.MAX_VALUE]。缓存相关参数，单位秒，默认为0。
                        .setTimeToLiveSeconds(-1))
                .setGroupConfig(gc)
                .setManagementCenterConfig(centerConfig)
                .addFlakeIdGeneratorConfig(flakeIdGeneratorConfig())
                // 设置为不可重入锁
                // .addLockConfig(fencedLockConfig)
        ;
        return config;
    }

    @Bean
    public FlakeIdGeneratorConfig flakeIdGeneratorConfig(){
        FlakeIdGeneratorConfig idGeneratorConfig = new FlakeIdGeneratorConfig(ID_GENERATOR);
        idGeneratorConfig.setPrefetchCount(10)
                .setPrefetchValidityMillis(MINUTES.toMillis(10));
        return idGeneratorConfig;
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }

    //@Bean
//    public HazelcastInstance hazelcastInstance() {
//        HazelcastInstance instance = HazelcastClient.newHazelcastClient(clientConfig());
//        return instance;
//    }
//
//
//
//    @Bean
//    public ClientFlakeIdGeneratorConfig clientFlakeIdGeneratorConfig(){
//        ClientFlakeIdGeneratorConfig idGeneratorConfig = new ClientFlakeIdGeneratorConfig(ID_GENERATOR);
//        idGeneratorConfig.setPrefetchCount(10)
//                .setPrefetchValidityMillis(MINUTES.toMillis(10));
//        return idGeneratorConfig;
//    }
//
//    /**
//     * 客户端配置，连接远程hazelcast服务器集群
//     * @return
//     */
//    @Bean
//    public ClientConfig clientConfig() {
//        ClientConfig clientConfig = new ClientConfig();
//        //集群组名称
//        clientConfig.getGroupConfig().setName("dev");
//        //节点地址
//        clientConfig.getNetworkConfig().addAddress("127.0.0.1:5701", "127.0.0.1:5702", "127.0.0.1:5703");
//        clientConfig.addFlakeIdGeneratorConfig(clientFlakeIdGeneratorConfig());
//        return clientConfig;
//    }
//
//
//    @Bean
//    public HazelcastInstance hazelcastInstance1(){
//        // return Hazelcast.newHazelcastInstance(hazelCastConfig()); // 本地启动hazelcast服务器
//        return HazelcastClient.newHazelcastClient(clientConfig()); // 连接远程hazelcast服务器
//    }
//
//    @Bean
//    public HazelcastInstance hazelcastInstance2(){
//        // return Hazelcast.newHazelcastInstance(hazelCastConfig()); // 本地启动hazelcast服务器
//        return HazelcastClient.newHazelcastClient(clientConfig()); // 连接远程hazelcast服务器
//    }
//
//    @Bean
//    public HazelcastInstance hazelcastInstance3(){
//        // return Hazelcast.newHazelcastInstance(hazelCastConfig()); // 本地启动hazelcast服务器
//        return HazelcastClient.newHazelcastClient(clientConfig()); // 连接远程hazelcast服务器
//    }

}