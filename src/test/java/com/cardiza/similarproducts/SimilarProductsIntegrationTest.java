package com.cardiza.similarproducts;

import com.cardiza.similarproducts.domain.model.ProductDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SimilarProductsIntegrationTest {

    static WireMockServer wireMockServer;
    static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    WebTestClient webTestClient;

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("clients.product-api.base-url",
                () -> "http://localhost:" + wireMockServer.port());
    }

    @Test
    void getSimilarProducts_200() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo("/product/1/similarids"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(List.of("2", "3")))));

        wireMockServer.stubFor(get(urlEqualTo("/product/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(
                                new ProductDetail("2", "prod2", 10.0, true)))));

        wireMockServer.stubFor(get(urlEqualTo("/product/3"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(
                                new ProductDetail("3", "prod3", 20.0, false)))));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(2);
    }

    @Test
    void getSimilarProducts_productNotFound_404() {
        wireMockServer.stubFor(get(urlEqualTo("/product/999/similarids"))
                .willReturn(aResponse().withStatus(404)));

        webTestClient.get()
                .uri("/product/999/similar")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getSimilarProducts_oneProductFails_returnsPartialList() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo("/product/1/similarids"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(List.of("2", "3")))));

        wireMockServer.stubFor(get(urlEqualTo("/product/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(
                                new ProductDetail("2", "prod2", 10.0, true)))));

        wireMockServer.stubFor(get(urlEqualTo("/product/3"))
                .willReturn(aResponse().withStatus(500)));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(1);
    }

    @Test
    void getSimilarProducts_emptyList_200() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo("/product/1/similarids"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("[]")));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(0);
    }

    @Test
    void getSimilarProducts_timeout_returnsEmptyList() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo("/product/1/similarids"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(List.of("2")))));

        wireMockServer.stubFor(get(urlEqualTo("/product/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withFixedDelay(2000)
                        .withBody(objectMapper.writeValueAsString(
                                new ProductDetail("2", "prod2", 10.0, true)))));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(0);
    }
}