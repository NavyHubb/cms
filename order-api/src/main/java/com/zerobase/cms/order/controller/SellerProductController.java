package com.zerobase.cms.order.controller;

import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.*;
import com.zerobase.cms.order.service.ProductItemService;
import com.zerobase.cms.order.service.ProductService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/product")
public class SellerProductController {

    private final ProductService productService;
    private final ProductItemService productItemService;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestHeader("X-AUTH-TOKEN") String token,
                                                 @RequestBody AddProductForm form) {
        return ResponseEntity.ok(
                ProductDto.from(productService.addProduct(provider.getUserVo(token).getId(), form)));
    }

    @PostMapping("/item")
    public ResponseEntity<ProductDto> addProductItem(@RequestHeader("X-AUTH-TOKEN") String token,
                                                     @RequestBody AddProductItemForm form) {
        return ResponseEntity.ok(
                ProductDto.from(productItemService.addProductItem(provider.getUserVo(token).getId(), form)));
    }

    @PutMapping
    public ResponseEntity<ProductDto> updateProduct(@RequestHeader("X-AUTH-TOKEN") String token,
                                                 @RequestBody UpdateProductForm form) {
        return ResponseEntity.ok(
                ProductDto.from(productService.updateProduct(provider.getUserVo(token).getId(), form)));
    }

    @PutMapping("/item")
    public ResponseEntity<ProductItemDto> updateProductItem(@RequestHeader("X-AUTH-TOKEN") String token,
                                                     @RequestBody UpdateProductItemForm form) {
        return ResponseEntity.ok(
                ProductItemDto.from(productItemService.updateProductItem(provider.getUserVo(token).getId(), form)));
    }
}
