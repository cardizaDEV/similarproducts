package com.cardiza.similarproducts.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "clients.product-api")
public class ProductApiProperties {

    private String baseUrl;
    private int timeout;
    private int maxRetries;
    private Endpoints endpoints;

    @Data
    public static class Endpoints {
        private String product;
        private String similarIds;
    }
}