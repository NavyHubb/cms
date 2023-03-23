package com.zerobase.cms.user.domain.seller;

import com.zerobase.cms.user.domain.model.Seller;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellerDto {
    private Long id;
    private String email;

    public static SellerDto from(Seller seller) {
        return new SellerDto(seller.getId(), seller.getEmail());
    }
}
