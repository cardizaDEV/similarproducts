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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private SimilarProductsClient similarClient;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnSimilarProductsInOrder() {

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
    void shouldIgnoreFailedProductCalls() {

        String productId = "1";
        List<String> ids = List.of("2", "3");

        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);

        when(similarClient.getSimilarIds(productId)).thenReturn(ids);
        when(productClient.getProduct("2")).thenReturn(p2);
        when(productClient.getProduct("3")).thenThrow(new RuntimeException());

        List<ProductDetail> result = productService.getSimilarProducts(productId);

        assertEquals(List.of(p2), result);
    }
}