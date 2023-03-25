package com.zerobase.cms.order.service;

import com.zerobase.cms.order.ZeroOrderApplication;
import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void ADD_PRODUCT_TEST() {
        // given
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("토니웩 해링턴자켓", "울 자켓", 3);

        // when
        Product p = productService.addProduct(sellerId, form);
        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        // then
        assertNotNull(result);
        assertEquals(result.getSellerId(), sellerId);
        assertEquals(result.getName(), "토니웩 해링턴자켓");
        assertEquals(result.getDescription(), "울 자켓");
        assertEquals(result.getProductItems().size(), 3);
        assertEquals(result.getProductItems().get(0).getSellerId(), sellerId);
        assertEquals(result.getProductItems().get(0).getName(), "토니웩 해링턴자켓0");
        assertEquals(result.getProductItems().get(0).getPrice(), 10000);
        assertEquals(result.getProductItems().get(0).getCount(), 1);
    }

    private static AddProductForm makeProductForm(String name, String description, int itemCount) {
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            itemForms.add(makeProductItemForm(null, name+i));
        }

        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(itemForms)
                .build();
    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(10000)
                .count(1)
                .build();
    }
}