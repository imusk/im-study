package com.github.imusk.im.study.mqtt.client.hivemq.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Musk
 * @date 2020-08-13 16:10
 * @email muskcool@protonmail.com
 * @description AcceptAllTrustManager
 */
public class AcceptAllTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}
