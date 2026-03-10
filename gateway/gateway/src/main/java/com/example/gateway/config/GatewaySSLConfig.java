package com.example.gateway.config;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import reactor.netty.http.client.HttpClient;

@Configuration
public class GatewaySSLConfig {

        @Value("${server.ssl.key-store}")
        private Resource keyStoreResource;

        @Value("${server.ssl.key-store-password}")
        private String keyStorePassword;

        @Value("${server.ssl.trust-store}")
        private Resource trustStoreResource;

        @Value("${server.ssl.trust-store-password}")
        private String trustStorePassword;

        @Bean
        public HttpClient httpClient() throws Exception {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(keyStoreResource.getInputStream(), keyStorePassword.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(keyStore, keyStorePassword.toCharArray());

                KeyStore trustStore = KeyStore.getInstance("PKCS12");
                trustStore.load(trustStoreResource.getInputStream(), trustStorePassword.toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(trustStore);

                SslContext nettySsl = SslContextBuilder
                                .forClient()
                                .keyManager(kmf)
                                .trustManager(tmf)
                                .build();

                return HttpClient.create()
                                .secure(spec -> spec.sslContext(nettySsl));
        }

        @Bean
        public ReactorClientHttpConnector clientConnector(HttpClient httpClient) {
                return new ReactorClientHttpConnector(httpClient);
        }
}