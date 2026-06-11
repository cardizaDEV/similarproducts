package com.cardiza.similarproducts.infrastructure.outbound.rest.port;

import reactor.core.publisher.Flux;

public interface SimilarProductsPort {
    Flux<String> getSimilarIds(String productId);
}