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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    void getSimilarProducts200() {
        String productId = "1";
        List<String> ids = List.of("2", "3");

        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);
        ProductDetail p3 = new ProductDetail("3", "prod3", 20.0, true);

        when(similarClient.getSimilarIds(productId)).thenReturn(ids);
        when(productClient.getProduct("2")).thenReturn(p2);
        when(productClient.getProduct("3")).thenReturn(p3);

        List<ProductDetail> result = productService.getSimilarProducts(productId);

        assertEquals(List.of(p2, p3), result);
    }

    @Test
    void getSimilarProductsPartialFailure() {
        String productId = "1";
        List<String> ids = List.of("2", "3");

        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);

        when(similarClient.getSimilarIds(productId)).thenReturn(ids);
        when(productClient.getProduct("2")).thenReturn(p2);
        when(productClient.getProduct("3")).thenThrow(new RuntimeException());

        List<ProductDetail> result = productService.getSimilarProducts(productId);

        assertEquals(List.of(p2), result);
    }

    @Test
    void getSimilarProductsAllFailed() {
        when(similarClient.getSimilarIds("1")).thenReturn(List.of("2", "3"));
        when(productClient.getProduct(anyString())).thenThrow(new RuntimeException());

        List<ProductDetail> result = productService.getSimilarProducts("1");

        assertTrue(result.isEmpty());
    }

    @Test
    void getSimilarProductsEmpty() {
        String productId = "1";

        when(similarClient.getSimilarIds(productId)).thenReturn(List.of());

        List<ProductDetail> result = productService.getSimilarProducts(productId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getSimilarProductsVerifyInteractions() {
        String productId = "1";
        List<String> ids = List.of("2");
        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);

        when(similarClient.getSimilarIds(productId)).thenReturn(ids);
        when(productClient.getProduct("2")).thenReturn(p2);

        productService.getSimilarProducts(productId);

        verify(similarClient).getSimilarIds(productId);
        verify(productClient).getProduct("2");
        verifyNoMoreInteractions(productClient, similarClient);
    }

    @Test
    void getSimilarProductsWithDuplicates() {
        String productId = "1";
        List<String> ids = List.of("2", "2");
        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);

        when(similarClient.getSimilarIds(productId)).thenReturn(ids);
        when(productClient.getProduct("2")).thenReturn(p2);

        List<ProductDetail> result = productService.getSimilarProducts(productId);

        assertEquals(List.of(p2, p2), result);
    }

    @Test
    void getSimilarProductsNullResponse() {
        when(similarClient.getSimilarIds("1")).thenReturn(null);

        List<ProductDetail> result = productService.getSimilarProducts("1");

        assertTrue(result.isEmpty());
    }
}