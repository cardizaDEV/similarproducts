package com.cardiza.similarproducts;

import com.cardiza.similarproducts.domain.model.ProductDetail;
import com.cardiza.similarproducts.exception.ProductNotFoundException;
import com.cardiza.similarproducts.infrastructure.outbound.rest.ProductClient;
import com.cardiza.similarproducts.infrastructure.outbound.rest.config.ProductApiProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductClientImplTest {

    @Mock
    private WebClient webClient;
    @Mock
    private ProductApiProperties props;
    @Mock
    private WebClient.RequestHeadersUriSpec<?> uriSpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> headersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ProductClient client;

    @Test
    void getProduct200() {
        ProductDetail expected = new ProductDetail("1", "prod", 10.0, true);

        when(props.getTimeout()).thenReturn(2000);
        when(props.getRetry()).thenReturn(1);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductDetail.class)).thenReturn(Mono.just(expected));

        StepVerifier.create(client.getProduct("1"))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void getProductEmptyBodyReturnsEmpty() {
        when(props.getTimeout()).thenReturn(2000);
        when(props.getRetry()).thenReturn(1);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductDetail.class)).thenReturn(Mono.empty());

        StepVerifier.create(client.getProduct("1"))
                .verifyComplete();
    }

    @Test
    void getProductNotFoundReturns404Exception() {
        when(props.getTimeout()).thenReturn(2000);
        when(props.getRetry()).thenReturn(0);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductDetail.class))
                .thenReturn(Mono.error(new ProductNotFoundException("1")));

        StepVerifier.create(client.getProduct("1"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getProductOtherErrorReturnsEmpty() {
        when(props.getTimeout()).thenReturn(2000);
        when(props.getRetry()).thenReturn(0);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductDetail.class))
                .thenReturn(Mono.error(new RuntimeException("server error")));

        StepVerifier.create(client.getProduct("1"))
                .verifyComplete();
    }

    @Test
    void getProductTimeoutReturnsEmpty() {
        when(props.getTimeout()).thenReturn(1);
        when(props.getRetry()).thenReturn(0);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductDetail.class))
                .thenReturn(Mono.error(new RuntimeException("timeout")));

        StepVerifier.create(client.getProduct("1"))
                .verifyComplete();
    }
}