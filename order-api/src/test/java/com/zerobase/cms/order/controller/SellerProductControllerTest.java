package com.zerobase.cms.order.controller;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.service.ProductItemService;
import com.zerobase.cms.order.service.ProductService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SellerProductController.class)
class SellerProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductItemService productItemService;

    @MockBean
    private JwtAuthenticationProvider provider;


    @Test
    @DisplayName("상품 추가")
    void addProduct() {
        //given
        given(productService.addProduct(anyLong(), any()))
                .willReturn(Product.builder()
                                .id(1L)
                                .build());

        //when
        //then
        mockMvc.perform(post("/seller/product")
                .header()
        )
    }

    @Test
    void addProductItem() {
        //given
        //when
        //then
    }

    @Test
    void updateProduct() {
        //given
        //when
        //then
    }

    @Test
    void updateProductItem() {
        //given
        //when
        //then
    }

    @Test
    void deleteProduct() {
        //given
        //when
        //then
    }

    @Test
    void deleteProductItem() {
        //given
        //when
        //then
    }
}