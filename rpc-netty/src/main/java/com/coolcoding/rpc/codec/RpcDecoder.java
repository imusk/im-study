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
