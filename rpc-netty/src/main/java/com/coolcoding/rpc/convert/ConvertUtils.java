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
