package com.zerobase.cms.order.appilication;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartApplicationTest {
    @Autowired
    private CartApplication cartApplication;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void ADD_TEST() {
        Product p = add_product();
        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        assertNotNull(result);

        assertEquals(result.getName(), "토니웩 해링턴자켓");
        assertEquals(result.getDescription(), "울 자켓");
        assertEquals(result.getProductItems().size(), 3);
        assertEquals(result.getProductItems().get(0).getName(), "토니웩 해링턴자켓0");
        assertEquals(result.getProductItems().get(0).getPrice(), 10000);

        Long customerId = 100L;

        // 데이터 잘 들어갔는지
        Cart cart = cartApplication.addCart(customerId, makeAddForm(result));
        assertEquals(cart.getMessages().size(), 0);

        cart = cartApplication.getCart(customerId);
        assertEquals(cart.getMessages().size(), 1);

        // 하위 필드들도 추가 구현
    }

    @Test
    void ADD_TEST_MODIFY() {
        Long customerId = 100L;

        cartApplication.clearCart(customerId);

        Product p = add_product();
        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        assertNotNull(result);

        assertEquals(result.getName(), "토니웩 해링턴자켓");
        assertEquals(result.getDescription(), "울 자켓");
        assertEquals(result.getProductItems().size(), 3);
        assertEquals(result.getProductItems().get(0).getName(), "토니웩 해링턴자켓0");
        assertEquals(result.getProductItems().get(0).getPrice(), 10000);

        // 데이터 잘 들어갔는지
        Cart cart = cartApplication.addCart(customerId, makeAddForm(result));
        assertEquals(cart.getMessages().size(), 0);

        cart = cartApplication.getCart(customerId);
        assertEquals(cart.getMessages().size(), 1);
    }

    AddProductCartForm makeAddForm(Product p) {
        AddProductCartForm.ProductItem productItem =
                AddProductCartForm.ProductItem.builder()
                        .id(p.getProductItems().get(0).getId())
                        .name(p.getProductItems().get(0).getName())
                        .count(1)
                        .price(2000)
                        .build();
        return AddProductCartForm.builder()
                .id(p.getId())
                .sellerId(p.getSellerId())
                .name(p.getName())
                .description(p.getDescription())
                .items(List.of(productItem))
                .build();
    }

    Product add_product() {
        Long sellerId = 1L;
        AddProductForm form = makeProductForm("토니웩 해링턴자켓", "울 자켓", 3);
        return productService.addProduct(sellerId, form);
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
                .count(5)
                .build();
    }
}