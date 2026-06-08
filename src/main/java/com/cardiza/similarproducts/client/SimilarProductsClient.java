package com.cardiza.similarproducts.client;

import reactor.core.publisher.Flux;

public interface SimilarProductsClient {
    Flux<String> getSimilarIds(String productId);
}