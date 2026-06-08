package com.cardiza.similarproducts;

import com.cardiza.similarproducts.client.ProductClient;
import com.cardiza.similarproducts.client.SimilarProductsClient;
import com.cardiza.similarproducts.dto.ProductDetail;
import com.cardiza.similarproducts.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private SimilarProductsClient similarClient;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private ProductService productService;

    private static final String PRODUCT_ID = "1";

    @Test
    void getSimilarProducts200() {
        List<String> ids = List.of("2", "3");

        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);
        ProductDetail p3 = new ProductDetail("3", "prod3", 20.0, true);

        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(ids);
        when(productClient.getProduct("2")).thenReturn(p2);
        when(productClient.getProduct("3")).thenReturn(p3);

        List<ProductDetail> result = productService.getSimilarProducts(PRODUCT_ID);

        assertEquals(2, result.size());
        assertEquals(List.of(p2, p3), result);
    }

    @Test
    void getSimilarProductsPartialFailure() {
        List<String> ids = List.of("2", "3");

        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);

        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(ids);
        when(productClient.getProduct("2")).thenReturn(p2);
        when(productClient.getProduct("3")).thenThrow(new RuntimeException());

        List<ProductDetail> result = productService.getSimilarProducts(PRODUCT_ID);

        assertEquals(1, result.size());
        assertTrue(result.contains(p2));
    }

    @Test
    void getSimilarProductsAllFailed() {
        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(List.of("2", "3"));
        when(productClient.getProduct(anyString())).thenThrow(new RuntimeException());

        List<ProductDetail> result = productService.getSimilarProducts(PRODUCT_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void getSimilarProductsEmpty() {

        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(List.of());

        List<ProductDetail> result = productService.getSimilarProducts(PRODUCT_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void getSimilarProductsVerifyInteractions() {
        List<String> ids = List.of("2");
        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);

        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(ids);
        when(productClient.getProduct("2")).thenReturn(p2);

        productService.getSimilarProducts(PRODUCT_ID);

        verify(similarClient).getSimilarIds(PRODUCT_ID);
        verify(productClient).getProduct("2");
        verifyNoMoreInteractions(productClient, similarClient);
    }

    @Test
    void getSimilarProductsWithDuplicates() {
        List<String> ids = List.of("2", "2");
        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);

        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(ids);
        when(productClient.getProduct("2")).thenReturn(p2);

        List<ProductDetail> result = productService.getSimilarProducts(PRODUCT_ID);

        assertEquals(2, result.size());
        assertEquals(List.of(p2, p2), result);
    }

    @Test
    void getSimilarProductsNullResponse() {
        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(null);

        List<ProductDetail> result = productService.getSimilarProducts(PRODUCT_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void getSimilarProductsWithNullProductResponse() {
        List<String> ids = List.of("2");

        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(ids);
        when(productClient.getProduct("2")).thenReturn(null);

        List<ProductDetail> result = productService.getSimilarProducts(PRODUCT_ID);

        assertTrue(result.isEmpty());
    }
}