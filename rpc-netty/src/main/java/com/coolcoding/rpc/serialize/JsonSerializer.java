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
