package com.zerobase.cms.order.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder  // @Getter 이외의 어노테이션은 테스트 코드 작성 시 모킹을 대신하기 위함
@NoArgsConstructor
@AllArgsConstructor
public class AddProductForm {
    private String name;
    private String description;
    private List<AddProductItemForm> items;
}
