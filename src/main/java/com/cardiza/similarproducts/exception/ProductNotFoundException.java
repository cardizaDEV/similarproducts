package com.cardiza.similarproducts.exception;

import static com.cardiza.similarproducts.values.ExceptionMessages.PRODUCT_NOT_FOUND;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productId) {
        super(String.format(PRODUCT_NOT_FOUND ,productId));
    }
}