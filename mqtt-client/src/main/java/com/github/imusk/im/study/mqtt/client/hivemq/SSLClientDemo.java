package com.github.imusk.im.study.mqtt.client.hivemq;

import com.github.imusk.im.study.mqtt.client.hivemq.ssl.HiveMQTTSSL;
import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.MqttWebSocketConfig;
import com.hivemq.client.mqtt.MqttWebSocketConfigBuilder;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.lifecycle.MqttClientAutoReconnect;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.Mqtt3ClientBuilder;
import com.hivemq.client.mqtt.mqtt3.message.auth.Mqtt3SimpleAuth;
import com.hivemq.client.mqtt.mqtt3.message.auth.Mqtt3SimpleAuthBuilder;
import com.hivemq.client.mqtt.mqtt3.message.connect.Mqtt3ConnectBuilder;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.Mqtt3Subscribe;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.Mqtt3SubscribeBuilder;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.Mqtt3Subscription;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAckReturnCode;
import org.fusesource.mqtt.client.MQTT;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Musk
 * @date 2020-08-13 11:34
 * @email muskcool@protonmail.com
 * @description HiveMQ MQTT SSL Client Demo
 */
public class SSLClientDemo {

    public static void main(String[] args) throws Exception {

        String address = "ssl://127.0.0.1:8883";

        String topic = "MQTT/TOPIC";

        String clientId = "MQTT_CLIENT_ID_" + System.currentTimeMillis();

        String username = "";
        String password = "";

        String protocol = "SSL";

        String host = "127.0.0.1";
        Integer port = 8883;

        String version = "3.1";
        String websocketPath = "/mqtt";

        Long keepAlive = 300L;
        Long connectMaxAttempts = 0L;
        Long reconnectMaxAttempts = 0L;

        Boolean isCleanSession = true;
        Long connectTimeout = 10L;

        // 是否配置证书
        Boolean isDualSSL = true;
        String certFilePath = "conf/server.pfx";
        String certPassword = "654321";

        Mqtt3ClientBuilder mqtt3ClientBuilder = Mqtt3Client.builder()
                .identifier(clientId)
                .serverHost(host)
                .serverPort(port);

        if (reconnectMaxAttempts > 0) {
            mqtt3ClientBuilder = mqtt3ClientBuilder.automaticReconnect(MqttClientAutoReconnect.builder().build());
        }

        if ("SSL".equals(protocol) || "WSS".equals(protocol)) {
            MqttClientSslConfig sslConfig = HiveMQTTSSL.createSsl(isDualSSL, certFilePath, certPassword);
//            MqttClientSslConfig sslConfig = HiveMQTTSSL.createSsl(false, "", "");
            mqtt3ClientBuilder = mqtt3ClientBuilder.sslConfig(sslConfig);
        }

        if ("WS".equals(protocol) || "WSS".equals(protocol)) {
            MqttWebSocketConfigBuilder wsConfigBuilder = MqttWebSocketConfig.builder();
            if (websocketPath != null) {
                wsConfigBuilder.serverPath(websocketPath);
            }
            mqtt3ClientBuilder = mqtt3ClientBuilder.webSocketConfig(wsConfigBuilder.build());
        }

        Mqtt3BlockingClient client = mqtt3ClientBuilder.buildBlocking();

        Mqtt3ConnectBuilder.Send<CompletableFuture<Mqtt3ConnAck>> connectSend = client.toAsync().connectWith()
                .cleanSession(isCleanSession)
                .keepAlive(keepAlive.intValue());

        // 认证
        Mqtt3SimpleAuth auth = null;
        if (username != null && !"".equalsIgnoreCase(username)) {
            Mqtt3SimpleAuthBuilder.Complete simpleAuth = Mqtt3SimpleAuth.builder().username(username);
            if (password != null && !"".equalsIgnoreCase(password)) {
                simpleAuth.password(password.getBytes());
            }
            auth = simpleAuth.build();
        }

        if (auth != null) {
            connectSend = connectSend.simpleAuth(auth);
        }

        System.out.println("Connect client: " + clientId);

        CompletableFuture<Mqtt3ConnAck> connectFuture = connectSend.send();

        Mqtt3ConnAck connAck = connectFuture.get(connectTimeout, TimeUnit.SECONDS);
        System.out.println("Connected client: " + clientId);

        if (!connAck.getReturnCode().isError()) {
            System.out.println("Connection established successfully.");
        } else {
            System.out.println("Failed to establish Connection.");
        }

        // 发布消息
        client.publishWith()
                .topic(topic)
                .payload(("Hello MQTT " + System.currentTimeMillis()).getBytes())
                .qos(MqttQos.AT_LEAST_ONCE)
                .retain(false)
                .send();

        // 订阅主题
//        String[] topicNames = {"MS", "MQTT/TOPIC"};
//
//        MqttQos hiveQos = MqttQos.AT_LEAST_ONCE;
//        Mqtt3Subscribe subscribe = null;
//        Mqtt3SubscribeBuilder builder = Mqtt3Subscribe.builder();
//        for (int i = 0; i < topicNames.length; i++) {
//            String topicName = topicNames[i];
//            Mqtt3Subscription subscription = Mqtt3Subscription.builder().topicFilter(topicName).qos(hiveQos).build();
//            if (i < topicNames.length - 1) {
//                builder.addSubscription(subscription);
//            } else {
//                subscribe = builder.addSubscription(subscription).build();
//            }
//        }
//
//        Mqtt3AsyncClient asyncClient = client.toAsync();
//        asyncClient.subscribe(subscribe, handlePublishReceived).whenComplete((ack, error) -> {
//            if (error != null) {
//                System.out.println("subscribe failed " + error);
//            } else {
//                List<Mqtt3SubAckReturnCode> ackCodes = ack.getReturnCodes();
//                for (int i = 0; i < ackCodes.size(); i++) {
//                    Mqtt3SubAckReturnCode ackCode = ackCodes.get(i);
//                    if (ackCode.isError()) {
//                        int index = i;
//                        System.out.println("Failed to subscribe " + topicNames[index] + " code: " + ackCode.name());
//                    }
//                }
//                System.out.println("sub successful, topic length is " + topicNames.length);
//            }
//        });


        client.disconnect();

        System.out.println("Close Connection.");;
    }

}
