package com.example.pract4_client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;

@Configuration
public class RSocketClientConfig {

    @Value("${rsocket.server.uri:ws://localhost:7000/rsocket}")
    private String rSocketUri;

    @Bean
    public RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder
                .websocket(URI.create(rSocketUri));
    }
}
