package com.cardiza.similarproducts.client.impl;

import com.cardiza.similarproducts.client.SimilarProductsClient;
import com.cardiza.similarproducts.config.ProductApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SimilarProductsClientImpl implements SimilarProductsClient {

    private final WebClient productWebClient;
    private final ProductApiProperties props;

    @Override
    public List<String> getSimilarIds(String productId) {
        return productWebClient.get()
                .uri(uriBuilder ->
                             uriBuilder
                                     .path(props.getEndpoints().getSimilarIds())
                                     .build(productId)
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .timeout(Duration.ofMillis(2000))
                .block();
    }
}