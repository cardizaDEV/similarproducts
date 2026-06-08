package com.cardiza.similarproducts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import io.netty.channel.ChannelOption;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient productWebClient(ProductApiProperties props) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(props.getTimeout()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getTimeout());

        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}