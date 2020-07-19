package com.wen.im.basic;

import com.wen.im.entity.MyUser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @classname: GeneratorJavaProto
 * @description: GeneratorJavaProto
 * @data: 2020-07-18 01:08
 * @author: Musk
 */
public class GeneratorJavaProto {


    public static void main(String[] args) {
        List<Class> classes = new ArrayList<>();
        classes.add(MyUser.class);
        java2proto(classes);
    }

    public static void java2proto(List<Class> classes){
        StringBuilder b = new StringBuilder();
        for (Class aClass : classes) {
            Field[] fields = aClass.getDeclaredFields();
            b.append("message ").append("Grpc").append(aClass.getSimpleName()).append("{").append("\n");
            for (int i = 1; i <= fields.length; i++) {
                Field field = fields[i-1];
                String name = field.getName();
                String type = field.getType().getName();
                b.append("    ");
                if("java.lang.String".equalsIgnoreCase(type)){
                    b.append("string");
                }else if("java.lang.Double".equalsIgnoreCase(type)){
                    b.append("double");
                }
                else if("java.util.Date".equalsIgnoreCase(type)){
                    b.append("string");
                }
                else if("java.lang.Long".equalsIgnoreCase(type)){
                    b.append("int64");
                }
                else if("java.lang.Integer".equalsIgnoreCase(type)){
                    b.append("int32");
                }
                else {
                    throw new RuntimeException(type+" 没处理");
                }
                b.append(" ").append(name).append(" ").append("=").append(" ").append(i).append(";");
                b.append("\n");
            }
            b.append("}");
            b.append("\n");
        }
        System.out.println(b);
    }



}
