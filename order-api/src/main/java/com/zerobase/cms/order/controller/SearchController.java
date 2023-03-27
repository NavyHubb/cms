package com.zerobase.cms.order.controller;

import com.zerobase.cms.order.domain.product.ProductDto;
import com.zerobase.cms.order.service.ProductSearchService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import feign.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search/product")
public class SearchController {
    private final ProductSearchService productSearchService;
    private final JwtAuthenticationProvider provider;

    @GetMapping
    public ResponseEntity<List<ProductDto>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(
                productSearchService.searchByName(name).stream()
                        .map(ProductDto::withoutItemsFrom).collect(Collectors.toList())  // items까지 가져올 필요는 없으니까 from 말고 withoutItemsFrom
        );
    }

    @GetMapping("/detail")
    public ResponseEntity<ProductDto> getDetail(@RequestParam Long productId) {
        return ResponseEntity.ok(
                ProductDto.from(productSearchService.getByProductId(productId))
        );
    }
}
