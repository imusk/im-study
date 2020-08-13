package com.github.imusk.im.study.protobuf.rabbitmq;

import com.github.imusk.im.study.proto.demo.AddressBookProto;
import com.github.imusk.im.study.protobuf.rabbitmq.utils.ConnectionUtil;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @classname: Sender
 * @description: Sender
 * @data: 2020-07-17 00:48
 * @author: Musk
 */
public class Sender {

    private final static String QUEUE = "MQ_Protobuf";//队列的名字

    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnectionUtil.getConnection();
        //创建通道
        Channel channel = connection.createChannel();

        //声明队列,如果队列存在则什么都不做,如果不存在才创建
        // 参数1 队列的名字
        //参数2 是否持久化队列,我们的队列模式是在内存中的,如果 rabbitmq 重启会丢失,如果我们设置为 true, 则会保存到 erlang 自带的数据库中,重启后会重新读取
        //参数3 是否排外,有两个作用,第一个当我们的连接关闭后是否会自动删除队列,作用二 是否私有当天前队列,如果私有了,其他通道不可以访问当前队列,如果为 true, 一般是一个队列只适用于一个消费者的时候
        //参数4 是否自动删除
        //参数5 我们的一些其他参数
        channel.queueDeclare(QUEUE, false, false, false, null);

        AddressBookProto.Person.Builder person =  AddressBookProto.Person.newBuilder();

        person.setId(1);
        person.setName("Lilei");
        person.setEmail("fqyang@163.com");

        AddressBookProto.Person.PhoneNumber.Builder MobileNumber = AddressBookProto.Person.PhoneNumber.newBuilder().setNumber("4099134756");
        MobileNumber.setType(AddressBookProto.Person.PhoneType.MOBILE);
        person.addPhones(MobileNumber);

        AddressBookProto.Person.PhoneNumber.Builder HomeNumber = AddressBookProto.Person.PhoneNumber.newBuilder().setNumber("10086");
        HomeNumber.setType(AddressBookProto.Person.PhoneType.HOME);
        person.addPhones(HomeNumber);

        byte[] MessageEntity = person.build().toByteArray();

        //发送内容
        channel.basicPublish("",QUEUE,null, MessageEntity);
        //关闭连接
        channel.close();
        connection.close();

    }

}
