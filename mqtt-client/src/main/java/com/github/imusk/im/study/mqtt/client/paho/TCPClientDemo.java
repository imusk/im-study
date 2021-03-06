package com.github.imusk.im.study.mqtt.client.paho;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @author: Musk
 * @data: 2020-08-13 01:56
 * @classname: MQTTClientDemo
 * @description: Paho MQTT TCP Client Demo
 */
public class TCPClientDemo {

    private static final String address = "tcp://127.0.0.1:1883";

    private static final String topic = "MQTT/TOPIC";

//    private static final String clientId = "MQTT_CLIENT_ID_" + System.currentTimeMillis();

    public static void main(String[] args) throws Exception {

        String clientId = "client_100001";
        MqttClient client = new MqttClient(address, clientId, new MemoryPersistence());
//        MqttClient client = new MqttClient(address, "client111", new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName("uid_100001");
        options.setPassword("123456".toCharArray());
        options.setMqttVersion(4);

        System.out.println("Connecting to broker: " + address);

        client.connect(options);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("Connect lost,do some thing to solve it");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                System.out.println("From topic: " + s);
                System.out.println("Message content: " + new String(mqttMessage.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

        System.out.println("已连接成功");

//        client.subscribe("IM/OFFLINE");
//
//        String message = "Hello MQTT, from TCP Client, Timestamp " + System.currentTimeMillis();
//        client.publish(topic, message.getBytes(), 1, false);

        //client.disconnect();
    }


}
