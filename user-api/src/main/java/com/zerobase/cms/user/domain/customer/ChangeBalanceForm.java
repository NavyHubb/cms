package com.zerobase.cms.user.domain.customer;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeBalanceForm {
    private String from;
    private String message;
    private Integer money;
}
