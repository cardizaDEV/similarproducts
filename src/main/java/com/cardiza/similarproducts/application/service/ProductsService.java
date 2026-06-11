package com.cardiza.similarproducts.application.service;

import com.cardiza.similarproducts.application.usecase.ProductsUseCase;
import com.cardiza.similarproducts.domain.model.ProductDetail;
import com.cardiza.similarproducts.exception.ProductNotFoundException;
import com.cardiza.similarproducts.infrastructure.outbound.rest.port.ProductPort;
import com.cardiza.similarproducts.infrastructure.outbound.rest.port.SimilarProductsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.cardiza.similarproducts.values.LogMessages.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductsService implements ProductsUseCase {

    private final SimilarProductsPort similarProductsPort;
    private final ProductPort productPort;

    @Override
    public Mono<List<ProductDetail>> getSimilarProducts(String productId) {
        log.debug(String.format(FETCHING_SIMILAR_PRODUCTS, productId));
        return similarProductsPort.getSimilarIds(productId)
                .distinct()
                .flatMap(id ->
                                 productPort.getProduct(id)
                                         .doOnSuccess(p -> log.debug(String.format(FETCHED_PRODUCT, id)))
                                         .onErrorResume(e -> {
                                             if (e instanceof ProductNotFoundException) return Mono.error(e);
                                             log.warn(String.format(FAILED_FETCH_PRODUCT, id, e.getMessage()));
                                             return Mono.empty();
                                         }),
                        50
                )
                .collectList()
                .onErrorResume(e -> {
                    if (e instanceof ProductNotFoundException) return Mono.error(e);
                    log.error(String.format(UNEXPECTED_ERROR, productId, e.getMessage()));
                    return Mono.just(List.of());
                });
    }
}