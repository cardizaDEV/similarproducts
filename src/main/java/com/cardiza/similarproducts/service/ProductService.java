package com.cardiza.similarproducts.service;

import com.cardiza.similarproducts.client.ProductClient;
import com.cardiza.similarproducts.client.SimilarProductsClient;
import com.cardiza.similarproducts.dto.ProductDetail;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductService {

    private final SimilarProductsClient similarClient;
    private final ProductClient productClient;

    public ProductService(SimilarProductsClient similarClient,
                          ProductClient productClient) {
        this.similarClient = similarClient;
        this.productClient = productClient;
    }

    public List<ProductDetail> getSimilarProducts(String productId) {

        List<String> ids = Optional.ofNullable(similarClient.getSimilarIds(productId))
                .orElseGet(List::of);

        if (ids.isEmpty()) {
            return List.of();
        }

        return ids.stream()
                .map(this::safeGetProduct)
                .filter(Objects::nonNull)
                .toList();
    }

    private ProductDetail safeGetProduct(String id) {
        try {
            return productClient.getProduct(id);
        } catch (Exception e) {
            return null;
        }
    }
}