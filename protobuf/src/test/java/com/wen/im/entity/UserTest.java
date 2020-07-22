package com.wen.im.entity;

import cn.wildfirechat.proto.ProtoConstants;
import com.alibaba.fastjson.JSON;
import com.google.protobuf.util.JsonFormat;
import com.wen.im.proto.test.MyUserProto;
import org.junit.jupiter.api.Test;

/**
 * @classname: UserTest
 * @description: UserTest
 * @data: 2020-07-18 23:43
 * @author: Musk
 */
class UserTest {

    @Test
    public void user() {

        MyUser user = new MyUser();
        user.setId(1L);
        user.setName("张三");
        user.setAge(18);
        user.setAddress(new MyUser.Address("中国", "福建省", "厦门市", "思明区", "361100"));
        user.setGender(MyUser.Gender.MALE);

        String json = JSON.toJSONString(user);

        System.out.println("用户：" + user);
        System.out.println("用户JSON："+ json);
    }

    @Test
    public void userProto() throws Exception {
        MyUserProto.MyUser.Builder myUser = MyUserProto.MyUser.newBuilder();

        MyUserProto.Address.Builder address = MyUserProto.Address.newBuilder();
        address.setCountry("中国");
        address.setProvince("福建");
        address.setCity("厦门");
        address.setPostcode("361100");

        myUser.setId(1L);
        myUser.setName("法外狂徒-张三");
        myUser.setAge(18);
        myUser.setAddress(address);
        myUser.setGender(MyUserProto.Gender.MALE);

        String myUserProtoJson = JsonFormat.printer().print(myUser);

        System.out.println("用户：" + myUser);
        System.out.println("性别：" + myUser.getGender());
        System.out.println("用户ProtoJson：" + myUserProtoJson);

        MyUser user = JSON.parseObject(myUserProtoJson, MyUser.class);

        System.out.println("MyUser："+ user);

    }

    @Test
    public void protoConstants() {
        int value = ProtoConstants.ChatroomState.Enum.Normal.getNumber();
        int ordinal = ProtoConstants.ChatroomState.Enum.Normal.ordinal();

        int osx = ProtoConstants.Platform.Enum.OSX_VALUE;

        int ios = ProtoConstants.PlatformEnum.Platform_iOS.ordinal();

        System.out.println(value);
        System.out.println(ordinal);
        System.out.println(osx);
        System.out.println(ios);

    }

}