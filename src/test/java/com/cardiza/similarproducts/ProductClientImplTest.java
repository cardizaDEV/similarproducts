package com.cardiza.similarproducts;

import com.cardiza.similarproducts.dto.ProductDetail;
import com.cardiza.similarproducts.client.ProductClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductClientTest {

    @Mock
    private ProductClient productClient;

    @Test
    void getProduct_ok() {
        ProductDetail expected = new ProductDetail("1", "prod", 10.0, true);

        when(productClient.getProduct("1")).thenReturn(Mono.just(expected));

        ProductDetail result = productClient.getProduct("1").block();

        assertEquals(expected, result);
        verify(productClient).getProduct("1");
    }

    @Test
    void getProduct_error() {
        when(productClient.getProduct("1"))
                .thenReturn(Mono.error(new RuntimeException("fail")));

        ProductDetail result = productClient.getProduct("1")
                .onErrorReturn((ProductDetail) null)
                .block();

        assertNull(result);
        verify(productClient).getProduct("1");
    }
}