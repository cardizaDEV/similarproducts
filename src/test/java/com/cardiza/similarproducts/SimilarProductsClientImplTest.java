package com.cardiza.similarproducts;

import com.cardiza.similarproducts.client.SimilarProductsClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimilarProductsClientTest {

    @Mock
    private SimilarProductsClient similarClient;

    @Test
    void getSimilarIds_ok() {
        when(similarClient.getSimilarIds("1"))
                .thenReturn(Flux.just("2", "3"));

        List<String> result = similarClient.getSimilarIds("1")
                .collectList()
                .block();

        assertEquals(List.of("2", "3"), result);
        verify(similarClient).getSimilarIds("1");
    }

    @Test
    void getSimilarIds_empty_on_error() {
        when(similarClient.getSimilarIds("1"))
                .thenReturn(Flux.error(new RuntimeException("fail")));

        List<String> result = similarClient.getSimilarIds("1")
                .onErrorResume(e -> Flux.empty())
                .collectList()
                .block();

        assertTrue(result.isEmpty());
        verify(similarClient).getSimilarIds("1");
    }
}