package com.cardiza.similarproducts.service;

import com.cardiza.similarproducts.client.ProductClient;
import com.cardiza.similarproducts.client.SimilarProductsClient;
import com.cardiza.similarproducts.dto.ProductDetail;
import com.cardiza.similarproducts.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

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
        return similarClient.getSimilarIds(productId)
                .distinct()
                .flatMap(id ->
                                 productClient.getProduct(id)
                                         .onErrorResume(e -> {
                                             if (e instanceof ProductNotFoundException) return Mono.error(e);
                                             return Mono.empty();
                                         }),
                        10
                )
                .collectList()
                .onErrorResume(e -> {
                    if (e instanceof ProductNotFoundException) return Mono.error(e);
                    return Mono.just(List.of());
                });
    }
}