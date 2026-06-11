package com.cardiza.similarproducts.infrastructure.outbound.rest.port;

import com.cardiza.similarproducts.domain.model.ProductDetail;
import reactor.core.publisher.Mono;

public interface ProductPort {
    Mono<ProductDetail> getProduct(String productId);
}