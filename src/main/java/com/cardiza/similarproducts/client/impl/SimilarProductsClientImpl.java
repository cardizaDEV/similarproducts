package com.cardiza.similarproducts.client.impl;

import com.cardiza.similarproducts.client.SimilarProductsClient;
import com.cardiza.similarproducts.config.ProductApiProperties;
import com.cardiza.similarproducts.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

import static com.cardiza.similarproducts.values.ExceptionMessages.UPSTREAM_ERROR_FOR_SIMILAR_IDS;

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
                .onStatus(
                        status -> status.value() == HttpStatus.NOT_FOUND.value(),
                        resp -> Mono.error(new ProductNotFoundException(productId))
                )
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> Mono.error(new RuntimeException(String.format(UPSTREAM_ERROR_FOR_SIMILAR_IDS + productId)))
                )
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .timeout(Duration.ofMillis(props.getTimeout()))
                .retryWhen(Retry.backoff(props.getRetry(), Duration.ofMillis(100))
                        .filter(e -> !(e instanceof ProductNotFoundException)))
                .flatMapMany(list -> {
                    if (list == null) return Flux.empty();
                    return Flux.fromIterable(list);
                })
                .onErrorResume(e -> {
                    if (e instanceof ProductNotFoundException) return Flux.error(e);
                    return Flux.empty();
                });
    }
}