package com.cardiza.similarproducts.service;

import com.cardiza.similarproducts.client.ProductClient;
import com.cardiza.similarproducts.client.SimilarProductsClient;
import com.cardiza.similarproducts.dto.ProductDetail;
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
                .flatMap(id ->
                                 productClient.getProduct(id)
                                         .onErrorResume(e -> Mono.empty())
                )
                .collectList();
    }
}