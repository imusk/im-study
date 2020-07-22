package com.coolcoding.rpc.serialize;

public class SerializerFactory {
    public static Serializer getSerializer() {
        return new JsonSerializer();
    }
}
