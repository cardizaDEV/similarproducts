package com.cardiza.similarproducts.service;

import com.cardiza.similarproducts.client.ProductClient;
import com.cardiza.similarproducts.client.SimilarProductsClient;
import com.cardiza.similarproducts.dto.ProductDetail;
import com.cardiza.similarproducts.exception.ProductNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.cardiza.similarproducts.values.LogMessages.*;

@Log4j2
@Service
public class ProductService {

    private final SimilarProductsClient similarClient;
    private final ProductClient productClient;

    public ProductService(SimilarProductsClient similarClient,
                          ProductClient productClient) {
        this.similarClient = similarClient;
        this.productClient = productClient;
    }

    public Mono<List<ProductDetail>> getSimilarProducts(String productId) {
        log.debug(String.format(FETCHING_SIMILAR_PRODUCTS, productId));
        return similarClient.getSimilarIds(productId)
                .distinct()
                .flatMap(id ->
                                 productClient.getProduct(id)
                                         .doOnSuccess(p -> log.debug(String.format(FETCHED_PRODUCT, id)))
                                         .onErrorResume(e -> {
                                             if (e instanceof ProductNotFoundException) return Mono.error(e);
                                             log.warn(String.format(FAILED_FETCH_PRODUCT, id, e.getMessage()));
                                             return Mono.empty();
                                         }),
                        50
                )
                .collectList()
                .onErrorResume(e -> {
                    if (e instanceof ProductNotFoundException) return Mono.error(e);
                    log.error(String.format(UNEXPECTED_ERROR, productId, e.getMessage()));
                    return Mono.just(List.of());
                });
    }
}