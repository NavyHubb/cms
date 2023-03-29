package com.zerobase.cms.order.client.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private Long id;
    private String email;
    private Integer balance;
}
