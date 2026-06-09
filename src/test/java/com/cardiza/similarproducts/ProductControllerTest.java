package com.cardiza.similarproducts;

import com.cardiza.similarproducts.controller.ProductController;
import com.cardiza.similarproducts.dto.ProductDetail;
import com.cardiza.similarproducts.exception.GlobalExceptionHandler;
import com.cardiza.similarproducts.exception.ProductNotFoundException;
import com.cardiza.similarproducts.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductService productService;

    @Test
    void getSimilarProducts200() {
        List<ProductDetail> products = List.of(
                new ProductDetail("2", "prod2", 10.0, true),
                new ProductDetail("3", "prod3", 20.0, false)
        );

        when(productService.getSimilarProducts("1")).thenReturn(Mono.just(products));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(2);
    }

    @Test
    void getSimilarProductsEmptyList() {
        when(productService.getSimilarProducts("1")).thenReturn(Mono.just(List.of()));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(0);
    }

    @Test
    void getSimilarProductsNotFound() {
        when(productService.getSimilarProducts("999"))
                .thenReturn(Mono.error(new ProductNotFoundException("999")));

        webTestClient.get()
                .uri("/product/999/similar")
                .exchange()
                .expectStatus().isNotFound();
    }
}