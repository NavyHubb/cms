package com.zerobase.cms.order.controller;

import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.product.ProductDto;
import com.zerobase.cms.order.domain.product.ProductItemDto;
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
        ProductDto productDto = ProductDto.from(productService.addProduct(provider.getUserVo(token).getId(), form));
        return ResponseEntity.ok(productDto);
    }

    @PostMapping("/item")
    public ResponseEntity<ProductDto> addProductItem(@RequestHeader("X-AUTH-TOKEN") String token,
                                                     @RequestBody AddProductItemForm form) {
        ProductDto productDto = ProductDto.from(productItemService.addProductItem(provider.getUserVo(token).getId(), form));
        return ResponseEntity.ok(productDto);
    }
}
