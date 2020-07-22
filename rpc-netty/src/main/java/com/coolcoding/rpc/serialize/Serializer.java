package com.coolcoding.rpc.serialize;

public interface Serializer {
    byte[] serialize(Object msg);
    <T> T deserialize(byte[] bytes, Class<T> clz);
}
