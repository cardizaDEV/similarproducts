package com.cardiza.similarproducts.client.impl;

import com.cardiza.similarproducts.client.SimilarProductsClient;
import com.cardiza.similarproducts.config.ProductApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SimilarProductsClientImpl implements SimilarProductsClient {

    private final WebClient productWebClient;
    private final ProductApiProperties props;

    @Override
    public Flux<String> getSimilarIds(String productId) {
        return productWebClient.get()
                .uri(uriBuilder ->
                             uriBuilder
                                     .path(props.getEndpoints().getSimilarIds())
                                     .build(productId)
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .timeout(Duration.ofMillis(props.getTimeout()))
                .retry(props.getRetry())
                .flatMapMany(list -> {
                    if (list == null) return Flux.empty();
                    return Flux.fromIterable(list);
                })
                .onErrorResume(e -> Flux.empty());
    }
}