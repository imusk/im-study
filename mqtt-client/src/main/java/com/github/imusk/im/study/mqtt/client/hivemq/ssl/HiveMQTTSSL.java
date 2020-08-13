package com.github.imusk.im.study.mqtt.client.hivemq.ssl;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.MqttClientSslConfigBuilder;
import com.hivemq.client.util.KeyStoreUtil;
import org.apache.jmeter.services.FileServer;

import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.util.Collections;

/**
 * @author Musk
 * @date 2020-08-13 16:10
 * @email muskcool@protonmail.com
 * @description HiveMQTTSSL
 */
public class HiveMQTTSSL {

    public static MqttClientSslConfig createSsl(Boolean isDualSSLAuth, String clientCertFilePath, String clientCertPassword) throws Exception {

        MqttClientSslConfigBuilder sslBuilder = MqttClientSslConfig.builder().protocols(Collections.singletonList("TLSv1.2"));

        if (!isDualSSLAuth) {
            System.out.println("Configured with non-dual SSL.");

            TrustManagerFactory acceptAllTmFactory = AcceptAllTrustManagerFactory.getInstance();
            sslBuilder = sslBuilder.trustManagerFactory(acceptAllTmFactory);

        } else {
            System.out.println("Configured with dual SSL, trying to load client certification.");

            // File keyStoreFile = Util.getKeyStoreFile(sampler);
            // String keyStorePass = sampler.getKeyStorePassword();
            File clientCertFile = getFilePath(clientCertFilePath);
            String keyStorePassword = clientCertPassword;
            String privateKeyPassword = clientCertPassword;

            MqttClientSslConfigBuilder mqttClientSslConfigBuilder = sslBuilder.keyManagerFactory(KeyStoreUtil.keyManagerFromKeystore(clientCertFile, keyStorePassword, privateKeyPassword));
            sslBuilder = mqttClientSslConfigBuilder.trustManagerFactory(AcceptAllTrustManagerFactory.getInstance());
        }

        return sslBuilder.build();
    }

    private static File getFilePath(String filePath) {
        File theFile = new File(filePath);

        return theFile;
    }

}
