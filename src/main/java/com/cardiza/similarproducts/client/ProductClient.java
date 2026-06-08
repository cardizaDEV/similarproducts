package com.cardiza.similarproducts.client;

import com.cardiza.similarproducts.dto.ProductDetail;
import reactor.core.publisher.Mono;

public interface ProductClient {
    Mono<ProductDetail> getProduct(String productId);
}