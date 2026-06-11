package com.cardiza.similarproducts.application.usecase;

import com.cardiza.similarproducts.domain.model.ProductDetail;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductsUseCase {
    Mono<List<ProductDetail>> getSimilarProducts(String productId);
}