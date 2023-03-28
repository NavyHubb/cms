package com.zerobase.cms.order.appilication;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.CartService;
import com.zerobase.cms.order.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartApplication {
    private final ProductSearchService productSearchService;
    private final CartService cartService;

    public Cart addCart(Long customerId, AddProductCartForm form) {
        // form을 통해 새로 추가하려는 상품의 id에 해당하는 상품 조회
        Product product = productSearchService.getByProductId(form.getId());
        if (product == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
        }

        // 고객 아이디에 해당하는 cart 조회
        Cart cart = cartService.getCart(customerId);
        // 상품 재고가 있는지부터 확인
        if (cart != null && !addAble(cart, product, form)) {
            throw new CustomException(ErrorCode.ITEM_COUNT_NOT_ENOUGH);
        }

        // 상품 추가
        return cartService.addCart(customerId, form);
    }

    public Cart putCart(Long customerId, Cart cart) {
        cartService.putCart(customerId, cart);
        return cart;
    }

    public Cart getCart(Long customerId) {
        Cart cart = refreshCart(cartService.getCart(customerId));

        Cart returnCart = new Cart();
        returnCart.setCustomerId(cart.getCustomerId());
        returnCart.setProducts(cart.getProducts());
        returnCart.setMessages(cart.getMessages());

        // 메세지를 한번 보내고 난 다음에는 다시 보일 필요가 없기 때문에
        // redis에 저장할 카트는 Messages 부분을 초기화
        cart.setMessages(new ArrayList<>());
        cartService.putCart(customerId, cart);

        return returnCart;
    }

    public void clearCart(Long customerId) {
        cartService.putCart(customerId, null);
    }

    private Cart refreshCart(Cart cart) {
        // Product 혹은 ProductItem에 대한 정보가 변경되었는지 확인하고 그에 맞는 알람을 제공

//        // Cart에 담겨있던 상품들
//        Map<Long, Cart.Product> productMap = cart.getProducts().stream()
//                .collect(Collectors.toMap(Cart.Product::getId, product -> product));
//
//        // Cart 내 상품들에 대한 현재 상품정보 조회
//        List<Product> products = productSearchService.getListByProductIds(new ArrayList<>(productMap.keySet()));

        Map<Long, Product> productMap = productSearchService.getListByProductIds(
                cart.getProducts().stream().map(Cart.Product::getId).collect(Collectors.toList()))
                        .stream()
                        .collect(Collectors.toMap(Product::getId, product -> product));

        // Cart 내 상품 정보와 해당 상품의 현재 정보가 일치하는지 비교
        for (int i = 0; i < cart.getProducts().size(); i++) {
            Cart.Product cartProduct = cart.getProducts().get(i);
            Product p = productMap.get(cartProduct.getId());

            if (p == null) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + " 상품이 삭제 되었습니다.");
                continue;
            }

            // Cart 내 특정 상품의 상품아이템 목록
            Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
                    .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

            List<String> tmpMessages = new ArrayList<>();
            for (int j = 0; j < cartProduct.getItems().size(); j++) {
                Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);
                ProductItem pi = productItemMap.get(cartProductItem.getId());

                if (pi == null) {
                    cartProduct.getItems().remove(cartProductItem);
                    j--;
                    cart.addMessage(cartProduct.getName() + " 옵션이 삭제 되었습니다.");
                    continue;
                }

                boolean isPriceChanged = false;
                boolean isCountNotEnough = false;

                if (!cartProductItem.getPrice().equals(pi.getPrice())) {
                    isPriceChanged = true;
                    cartProductItem.setPrice(pi.getPrice());
                }

                if (cartProductItem.getCount() > pi.getCount()) {
                    isCountNotEnough = true;
                    cartProductItem.setCount(pi.getCount());
                }

                if (isPriceChanged && isCountNotEnough) {
                    tmpMessages.add(cartProductItem.getName() + "가격변동 및 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                } else if (isPriceChanged) {
                    tmpMessages.add(cartProductItem.getName() + "가격이 변동되었습니다.");
                } else if (isCountNotEnough) {
                    tmpMessages.add(cartProductItem.getName() + "가격변동 및 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                }
            }

            // 위의 로직을 거치면서 ProductItem이 모두 삭제되어버림. 그럼 Product도 삭제 진행시켜
            if (cartProduct.getItems().size() == 0) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + " 상품의 옵션이 모두 없어져 구매가 불가능합니다.");
                continue;
            } else if (tmpMessages.size() > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(cartProduct.getName() + "상품의 변동 사항 : ");
                for (String message : tmpMessages) {
                    sb.append(message);
                    sb.append(", ");
                }
                cart.addMessage(sb.toString());
            }
        }
        cartService.putCart(cart.getCustomerId(), cart);
        return cart;
    }

    // 카트에 추가할 수 있을만큼 수량이 충분한지 확인. 반환값이 true이면 add할 수 없는 것
    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
        // 카트에 있는 상품 중 새로 카트에 추가할 상품과 중복되는 상품이 있는지 확인하고, 없으면 새로 생성
        Cart.Product cartProduct = cart.getProducts().stream()
                .filter(p -> p.getId().equals(form.getId()))
                .findFirst()
                .orElse(Cart.Product.builder()
                        .id(product.getId())
                        .items(Collections.emptyList()).build());

        // cartProduct의 아이템 별 갯수
        Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
                .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

        // 새로 추가할 상품의 아이템 별 재고 갯수
        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));


        return form.getItems().stream().noneMatch(  // noneMath(): 내부 로직에서 true가 한번이라도 나오면 false
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    if (cartCount == null) {
                        cartCount = 0;
                    }

                    Integer currentCount = currentItemCountMap.get(formItem.getId());

                    // (Cart에 새로 추가하려는 수량) + (이미 Cart에 담겨있는 수량) > (현재 재고)
                    return formItem.getCount() + cartCount > currentCount;
                }
        );
    }
}
