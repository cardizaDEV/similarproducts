package com.cardiza.similarproducts;

import com.cardiza.similarproducts.client.ProductClient;
import com.cardiza.similarproducts.client.SimilarProductsClient;
import com.cardiza.similarproducts.dto.ProductDetail;
import com.cardiza.similarproducts.exception.ProductNotFoundException;
import com.cardiza.similarproducts.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);
        ProductDetail p3 = new ProductDetail("3", "prod3", 20.0, true);

        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(Flux.just("2", "3"));
        when(productClient.getProduct("2")).thenReturn(Mono.just(p2));
        when(productClient.getProduct("3")).thenReturn(Mono.just(p3));

        StepVerifier.create(productService.getSimilarProducts(PRODUCT_ID))
                .assertNext(list -> {
                    assert list.size() == 2;
                    assert list.contains(p2);
                    assert list.contains(p3);
                })
                .verifyComplete();
    }

    @Test
    void getSimilarProductsPartialFailure() {
        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);

        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(Flux.just("2", "3"));
        when(productClient.getProduct("2")).thenReturn(Mono.just(p2));
        when(productClient.getProduct("3")).thenReturn(Mono.error(new RuntimeException()));

        StepVerifier.create(productService.getSimilarProducts(PRODUCT_ID))
                .assertNext(list -> {
                    assert list.size() == 1;
                    assert list.contains(p2);
                })
                .verifyComplete();
    }

    @Test
    void getSimilarProductsAllFailed() {
        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(Flux.just("2", "3"));
        when(productClient.getProduct(anyString())).thenReturn(Mono.error(new RuntimeException()));

        StepVerifier.create(productService.getSimilarProducts(PRODUCT_ID))
                .assertNext(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void getSimilarProductsEmpty() {
        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(Flux.empty());

        StepVerifier.create(productService.getSimilarProducts(PRODUCT_ID))
                .assertNext(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void getSimilarProductsDuplicateIdsDeduped() {
        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);

        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(Flux.just("2", "2"));
        when(productClient.getProduct("2")).thenReturn(Mono.just(p2));

        StepVerifier.create(productService.getSimilarProducts(PRODUCT_ID))
                .assertNext(list -> {
                    assert list.size() == 1;
                    assert list.get(0).equals(p2);
                })
                .verifyComplete();
    }

    @Test
    void getSimilarProductsWithNullProductResponse() {
        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(Flux.just("2"));
        when(productClient.getProduct("2")).thenReturn(Mono.empty());

        StepVerifier.create(productService.getSimilarProducts(PRODUCT_ID))
                .assertNext(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void getSimilarProducts_skips_empty_products() {
        ProductDetail p2 = new ProductDetail("2", "prod2", 10.0, true);

        when(similarClient.getSimilarIds(PRODUCT_ID)).thenReturn(Flux.just("2", "3"));
        when(productClient.getProduct("2")).thenReturn(Mono.just(p2));
        when(productClient.getProduct("3")).thenReturn(Mono.empty());

        StepVerifier.create(productService.getSimilarProducts(PRODUCT_ID))
                .assertNext(list -> {
                    assert list.size() == 1;
                    assert list.get(0).equals(p2);
                })
                .verifyComplete();
    }

    @Test
    void getSimilarProductsMainProductNotFoundPropagates() {
        when(similarClient.getSimilarIds(PRODUCT_ID))
                .thenReturn(Flux.error(new ProductNotFoundException(PRODUCT_ID)));

        StepVerifier.create(productService.getSimilarProducts(PRODUCT_ID))
                .expectError(ProductNotFoundException.class)
                .verify();
    }
}