package com.cardiza.similarproducts.client;

import com.cardiza.similarproducts.dto.ProductDetail;

public interface ProductClient {
    ProductDetail getProduct(String productId);
}