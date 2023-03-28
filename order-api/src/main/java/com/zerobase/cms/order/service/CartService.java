package com.zerobase.cms.order.service;

import com.zerobase.cms.order.client.RedisClient;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final RedisClient redisClient;

    public Cart getCart(Long customerId) {
        Cart cart = redisClient.get(customerId, Cart.class);
        return cart != null ? cart : new Cart(customerId);
    }

    public Cart putCart(Long customerId, Cart cart) {
        redisClient.put(customerId, cart);
        return cart;
    }

    public Cart addCart(Long customerId, AddProductCartForm form) {
        // 고객 아이디에 해당하는 Cart 조회
        Cart cart = redisClient.get(customerId, Cart.class);

        // 기존 Cart가 없으면 새로 만들어
        if (cart == null) {
            cart = new Cart();
            cart.setCustomerId(customerId);
        }

        // Cart에 id가 중복되는 상품이 있는지 확인
        Optional<Cart.Product> productOptional = cart.getProducts().stream()
                .filter(p -> p.getId().equals(form.getId()))
                .findFirst();

        if (productOptional.isPresent()) {  // 중복되는 id의 상품이 존재하는 경우
            Cart.Product redisProduct = productOptional.get();

            // 요청한 아이템
            List<Cart.ProductItem> items = form.getItems().stream()
                    .map(Cart.ProductItem::from)
                    .collect(Collectors.toList());
            Map<Long, Cart.ProductItem> redisItemMap = redisProduct.getItems().stream()
                    .collect(Collectors.toMap(i -> i.getId(), i -> i));  // Map을 사용하는 이유는 검색속도 향상

            if (!redisProduct.getName().equals(form.getName())) {  // 상품 id는 같은데 name은 다른 경우
                cart.addMessage(redisProduct.getName() + "의 정보가 변경되었습니다. 확인 부탁드립니다.");
            }

            for (Cart.ProductItem item : items) {
                Cart.ProductItem redisItem = redisItemMap.get(item.getId());

                if (redisItem == null) {
                    redisProduct.getItems().add(item);
                } else {
                    if (!redisItem.getPrice().equals(item.getPrice())) {
                        cart.addMessage(redisProduct.getName() + item.getName() + "의 가격이 변경되었습니다. 확인 부탁드립니다.");
                    }
                    redisItem.setCount(redisItem.getCount() + item.getCount());
                }
            }
        } else {  // 중복되는 id의 상품이 존재하지 않는 경우
            Cart.Product product = Cart.Product.from(form);
            cart.getProducts().add(product);
        }

        redisClient.put(customerId, cart);  // 고객 별 Cart 정보를 redis에 저장

        return cart;
    }
}
