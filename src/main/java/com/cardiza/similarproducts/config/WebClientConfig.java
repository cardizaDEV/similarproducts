package com.cardiza.similarproducts.config;

import io.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Hooks;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import io.netty.channel.ChannelOption;

import static com.cardiza.similarproducts.values.ExceptionMessages.UNEXPECTED_DROPPED_ERROR;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient productWebClient(ProductApiProperties props) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(props.getTimeout()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getTimeout())
                .option(ChannelOption.SO_KEEPALIVE, true);

        Hooks.onErrorDropped(e -> {
            if (e instanceof ReadTimeoutException ||
                        e.getCause() instanceof ReadTimeoutException) {
                return;
            }
            LoggerFactory.getLogger(WebClientConfig.class).error(String.format(UNEXPECTED_DROPPED_ERROR, e));
        });

        Hooks.onErrorDropped(e -> {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof ReadTimeoutException) return;
            LoggerFactory.getLogger(WebClientConfig.class).error("Unexpected dropped error", e);
        });

        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}