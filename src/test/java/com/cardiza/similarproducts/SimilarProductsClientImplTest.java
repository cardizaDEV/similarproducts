package com.cardiza.similarproducts;

import com.cardiza.similarproducts.client.impl.SimilarProductsClientImpl;
import com.cardiza.similarproducts.config.ProductApiProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimilarProductsClientImplTest {

    @Mock private WebClient webClient;
    @Mock private ProductApiProperties props;

    @Mock private WebClient.RequestHeadersUriSpec<?> uriSpec;
    @Mock private WebClient.RequestHeadersSpec<?> headersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private SimilarProductsClientImpl client;

    @Test
    void getSimilarIds200() {
        doReturn(2000).when(props).getTimeout();
        doReturn(1).when(props).getRetry();

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        List<String> response = List.of("2", "3");

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(client.getSimilarIds("1"))
                .expectNext("2", "3")
                .verifyComplete();
    }

    @Test
    void getSimilarIdsNullList() {
        doReturn(2000).when(props).getTimeout();
        doReturn(1).when(props).getRetry();

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.justOrEmpty(null));

        StepVerifier.create(client.getSimilarIds("1"))
                .verifyComplete();
    }

    @Test
    void getSimilarIdsExceptionHandledAsEmpty() {
        doReturn(2000).when(props).getTimeout();
        doReturn(1).when(props).getRetry();

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(new RuntimeException("fail")));

        StepVerifier.create(client.getSimilarIds("1"))
                .verifyComplete();
    }
}