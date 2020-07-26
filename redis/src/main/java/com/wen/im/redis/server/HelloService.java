package com.wen.im.redis.server;

import com.alibaba.fastjson.JSONObject;
import com.wen.im.redis.utils.LettuceRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * @classname: HelloService
 * @description: HelloService
 * @data: 2020-07-26 19:05
 * @author: Musk
 */
@Service
public class HelloService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ValueOperations valueOperations;

    /**
     * 使用 SpringBoot 自带的Cache缓存，并使用Redis作为缓存引擎，在application.yml当中配置
     * @param id
     * @return
     */
    @Cacheable(value = "test", key = "'id_' + #id")
    public String a(String id) {
        System.out.println("缓存过期或不存在，重新执行业务逻辑，并写入缓存");
        JSONObject data = new JSONObject();
        data.put("code", 200);
        data.put("msg", "success");
        data.put("data", id);
        return data.toJSONString();
    }


    public String b(String id) {
        JSONObject data = (JSONObject) valueOperations.get(id);
        if (data == null) {
            System.out.println("没有走缓存");
            data = new JSONObject();
            data.put("code", 200);
            data.put("msg", "success");
            data.put("data", id);
            valueOperations.set(id, data);
        } else {
            System.out.println("有缓存");
        }

        return data.toJSONString();
    }

    public String c(String id) {
        String json = LettuceRedisUtil.getString(id);
        if (json == null) {
            System.out.println("没有走缓存");
            JSONObject data = new JSONObject();
            data.put("code", 200);
            data.put("msg", "success");
            data.put("data", id);
            json = data.toJSONString();
            LettuceRedisUtil.setString(id, data.toJSONString());
        } else {
            System.out.println("有缓存");
        }

        return json;
    }



}
