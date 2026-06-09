package com.cardiza.similarproducts.client.impl;

import com.cardiza.similarproducts.client.ProductClient;
import com.cardiza.similarproducts.config.ProductApiProperties;
import com.cardiza.similarproducts.dto.ProductDetail;
import com.cardiza.similarproducts.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.cardiza.similarproducts.values.ExceptionMessages.UPSTREAM_ERROR_FOR_PRODUCT;

@Service
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient {

    private final WebClient productWebClient;
    private final ProductApiProperties props;

    @Override
    public Mono<ProductDetail> getProduct(String productId) {
        return productWebClient.get()
                .uri(uriBuilder ->
                             uriBuilder
                                     .path(props.getEndpoints().getProduct())
                                     .build(productId)
                )
                .retrieve()
                .onStatus(
                        status -> status.value() == HttpStatus.NOT_FOUND.value(),
                        resp -> Mono.error(new ProductNotFoundException(productId))
                )
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> Mono.error(new RuntimeException(String.format(UPSTREAM_ERROR_FOR_PRODUCT, productId)))
                )
                .bodyToMono(ProductDetail.class)
                .timeout(Duration.ofMillis(props.getTimeout()))
                .retry(props.getRetry())
                .onErrorResume(e -> {
                    if (e instanceof ProductNotFoundException) return Mono.error(e);
                    return Mono.empty();
                });
    }
}