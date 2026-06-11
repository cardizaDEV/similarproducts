package com.cardiza.similarproducts.infrastructure.outbound.rest;

import com.cardiza.similarproducts.domain.model.ProductDetail;
import com.cardiza.similarproducts.exception.ProductNotFoundException;
import com.cardiza.similarproducts.infrastructure.outbound.rest.config.ProductApiProperties;
import com.cardiza.similarproducts.infrastructure.outbound.rest.port.ProductPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static com.cardiza.similarproducts.values.ExceptionMessages.UPSTREAM_ERROR_FOR_PRODUCT;
import static com.cardiza.similarproducts.values.LogMessages.FAILED_FETCH_PRODUCT_CLIENT;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductClient implements ProductPort {

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
                .retryWhen(Retry.backoff(props.getRetry(), Duration.ofMillis(100))
                        .filter(e -> !(e instanceof ProductNotFoundException)))
                .onErrorResume(e -> {
                    if (e instanceof ProductNotFoundException) return Mono.error(e);
                    log.warn(String.format(FAILED_FETCH_PRODUCT_CLIENT, productId,
                            e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
                    return Mono.empty();
                });
    }
}