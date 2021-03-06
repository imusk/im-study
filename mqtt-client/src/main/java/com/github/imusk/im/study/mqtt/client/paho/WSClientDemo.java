package com.github.imusk.im.study.mqtt.client.paho;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @author: Musk
 * @data: 2020-08-13 01:56
 * @classname: MQTTClientDemo
 * @description: Paho MQTT WS Client Demo
 */
public class WSClientDemo {

    private static final String address = "ws://127.0.0.1:1884/mqtt";

    private static final String topic = "MQTT/TOPIC";

    private static final String clientId = "MQTT_CLIENT_ID_" + System.currentTimeMillis();

    public static void main(String[] args) throws Exception {

        MqttClient client = new MqttClient(address, clientId, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);

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

        client.subscribe(topic);

        String message = "Hello MQTT, from WS Client, Timestamp " + System.currentTimeMillis();
        client.publish(topic, message.getBytes(), 1, false);

        //client.disconnect();
    }


}
