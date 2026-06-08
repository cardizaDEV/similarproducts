package com.cardiza.similarproducts.controller;

import com.cardiza.similarproducts.dto.ProductDetail;
import com.cardiza.similarproducts.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}/similar")
    public List<ProductDetail> getSimilarProducts(@PathVariable String productId) {
        return productService.getSimilarProducts(productId);
    }
}