package com.github.imusk.im.study.mqtt.client.hivemq.ssl;

import javax.net.ssl.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.cert.X509Certificate;

/**
 * @author Musk
 * @date 2020-08-13 16:10
 * @email muskcool@protonmail.com
 * @description AcceptAllTrustManagerFactory
 */
public class AcceptAllTrustManagerFactory extends TrustManagerFactory {

    private static final Provider PROVIDER = new Provider("", 0.0, "") {
    };

    private AcceptAllTrustManagerFactory() {
        super(AcceptAllTrustManagerFactorySpi.getInstance(), PROVIDER, "");
    }

    public static final TrustManagerFactory getInstance() {
        return new AcceptAllTrustManagerFactory();
    }

    static final class AcceptAllTrustManagerFactorySpi extends TrustManagerFactorySpi {

        public static final AcceptAllTrustManagerFactorySpi getInstance() {
            return new AcceptAllTrustManagerFactorySpi();
        }

        @Override
        protected TrustManager[] engineGetTrustManagers() {
            System.out.println("!! get trust managers (X509)");
            return new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
        }

        @Override
        protected void engineInit(KeyStore ks) throws KeyStoreException {
        }

        @Override
        protected void engineInit(ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {
        }

    }

}
