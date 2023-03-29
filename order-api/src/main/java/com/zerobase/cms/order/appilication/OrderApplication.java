package com.zerobase.cms.order.appilication;

import com.zerobase.cms.order.client.MailgunClient;
import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomerDto;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.mailgun.SendMailForm;
import com.zerobase.cms.order.service.ProductItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderApplication {

    private final CartApplication cartApplication;
    private final ProductItemService productItemService;
    private final UserClient userClient;
    private final MailgunClient mailgunClient;

    @Transactional
    public void order(String token, Cart cart) {
        // 상품 최신 상태 확인
        Cart orderCart = cartApplication.refreshCart(cart);
        if (orderCart.getMessages().size() > 0) {
            throw new CustomException(ErrorCode.ORDER_FAIL_CHECK_CART_FAIL);
        }

        // 고객의 잔액이 충분한지 확인
        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();
        int totalPrice = getTotalPrice(cart);
        if (customerDto.getBalance() < totalPrice) {
            throw new CustomException(ErrorCode.ORDER_FAIL_NO_MONEY);
        }

        // 결제(잔액 차감)
        userClient.changeBalance(token,
                ChangeBalanceForm.builder()
                        .from("USER")
                        .message("Order")
                        .money(-totalPrice)
                        .build());

        // 재고 차감
        for (Cart.Product product : orderCart.getProducts()) {
            for (Cart.ProductItem cartItem : product.getItems()) {
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount() - cartItem.getCount());
            }
        }

        // 주문 내역 이메일 발송
        String email = customerDto.getEmail();
        sendResultEmail(email, cart);
    }

    // 주문 내역 이메일로 발송
    private void sendResultEmail(String email, Cart cart) {
        SendMailForm sendMailForm = SendMailForm.builder()
                .from("tester@green.com")
                .to(email)
                .subject("[GreenCommerce] 감사합니다. 주문이 정상적으로 완료 되었습니다.")
                .text(getEmailBody(cart))
                .build();

        // 사용자에게 인증메일 발송
        log.info("Send email result : " + mailgunClient.sendEmail(sendMailForm).getBody());
    }

    private String getEmailBody(Cart cart) {
        // 메일 본문 내용
        StringBuilder sb = new StringBuilder();
        sb.append("고객님이 주문이 접수되었습니다.\n\n")
          .append("주문내역 [상품명(수량)]\n");
        for (Cart.Product product : cart.getProducts()) {
            sb.append("- " + product.getName() + "(" + product.getItems().size() + ")\n");
        }

        return sb.toString();
    }


    // 카트 내 상품 가격의 총합 구하기
    private Integer getTotalPrice(Cart cart) {
        return cart.getProducts().stream()
                .flatMapToInt(product -> product.getItems().stream()
                        .flatMapToInt(productItem -> IntStream.of(productItem.getPrice() * productItem.getCount())))
                .sum();
    }

    // 결제를 위해 필요한 것
    // 1. 상품들이 전부 주문 가능한 상태인지
    // 2. 가격 변동이 있었는지
    // 3. 고객의 돈이 충분한지
    // 4. 결제 & 상품의 재고 관리
}
