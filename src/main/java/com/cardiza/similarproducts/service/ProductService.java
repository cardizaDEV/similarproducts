package com.cardiza.similarproducts.service;

import com.cardiza.similarproducts.client.ProductClient;
import com.cardiza.similarproducts.client.SimilarProductsClient;
import com.cardiza.similarproducts.dto.ProductDetail;
import org.springframework.stereotype.Service;

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

    public List<ProductDetail> getSimilarProducts(String productId) {

        List<String> ids = similarClient.getSimilarIds(productId);

        return ids.stream()
                .map(id -> {
                    try {
                        return productClient.getProduct(id);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}