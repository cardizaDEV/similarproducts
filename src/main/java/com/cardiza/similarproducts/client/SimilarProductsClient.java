package com.cardiza.similarproducts.client;

import java.util.List;

public interface SimilarProductsClient {
    List<String> getSimilarIds(String productId);
}