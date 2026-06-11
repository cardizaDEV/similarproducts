package com.cardiza.similarproducts.infrastructure.inbound.rest;

import com.cardiza.similarproducts.application.usecase.ProductsUseCase;
import com.cardiza.similarproducts.domain.model.ProductDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.cardiza.similarproducts.values.MappingValues.GET_SIMILAR_PRODUCTS_URL;
import static com.cardiza.similarproducts.values.MappingValues.PRODUCT_URL;

@RestController
@RequestMapping(PRODUCT_URL)
@RequiredArgsConstructor
public class ProductController {

    private final ProductsUseCase productService;

    @GetMapping(GET_SIMILAR_PRODUCTS_URL)
    public Mono<List<ProductDetail>> getSimilarProducts(@PathVariable String productId) {
        return productService.getSimilarProducts(productId);
    }
}