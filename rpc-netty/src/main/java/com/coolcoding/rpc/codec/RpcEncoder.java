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
