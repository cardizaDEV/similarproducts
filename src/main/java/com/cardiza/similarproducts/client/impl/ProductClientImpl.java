package com.cardiza.similarproducts.client.impl;

import com.cardiza.similarproducts.client.ProductClient;
import com.cardiza.similarproducts.config.ProductApiProperties;
import com.cardiza.similarproducts.dto.ProductDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient {

    private final WebClient productWebClient;
    private final ProductApiProperties props;

    @Override
    public ProductDetail getProduct(String productId) {

        return productWebClient.get()
                .uri(uriBuilder ->
                             uriBuilder
                                     .path(props.getEndpoints().getProduct())
                                     .build(productId)
                )
                .retrieve()
                .bodyToMono(ProductDetail.class)
                .timeout(Duration.ofMillis(2000))
                .block();
    }
}