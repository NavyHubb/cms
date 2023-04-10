package com.zerobase.cms.order.controller;

import com.zerobase.cms.order.domain.product.*;
import com.zerobase.cms.order.service.ProductItemService;
import com.zerobase.cms.order.service.ProductService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
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

    private final String TOKEN_NAME = "X-AUTH-TOKEN";

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestHeader(TOKEN_NAME) String token,
                                                 @RequestBody AddProductForm form) {
        return ResponseEntity.ok(
                ProductDto.from(productService.addProduct(provider.getUserVo(token).getId(), form)));
    }

    @PostMapping("/item")
    public ResponseEntity<ProductDto> addProductItem(@RequestHeader(TOKEN_NAME) String token,
                                                     @RequestBody AddProductItemForm form) {
        return ResponseEntity.ok(
                ProductDto.from(productItemService.addProductItem(provider.getUserVo(token).getId(), form)));
    }

    @PutMapping
    public ResponseEntity<ProductDto> updateProduct(@RequestHeader(TOKEN_NAME) String token,
                                                 @RequestBody UpdateProductForm form) {
        return ResponseEntity.ok(
                ProductDto.from(productService.updateProduct(provider.getUserVo(token).getId(), form)));
    }

    @PutMapping("/item")
    public ResponseEntity<ProductItemDto> updateProductItem(@RequestHeader(TOKEN_NAME) String token,
                                                     @RequestBody UpdateProductItemForm form) {
        return ResponseEntity.ok(
                ProductItemDto.from(productItemService.updateProductItem(provider.getUserVo(token).getId(), form)));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@RequestHeader(TOKEN_NAME) String token,
                                                    @RequestParam Long id) {
        productService.deleteProduct(provider.getUserVo(token).getId(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/item")
    public ResponseEntity<Void> deleteProductItem(@RequestHeader(TOKEN_NAME) String token,
                                                            @RequestParam Long id) {
        productItemService.deleteProductItem(provider.getUserVo(token).getId(), id);
        return ResponseEntity.ok().build();
    }
}
