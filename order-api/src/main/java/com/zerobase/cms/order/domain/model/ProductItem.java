package com.zerobase.cms.order.domain.model;

import com.zerobase.cms.order.domain.product.AddProductItemForm;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class ProductItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    @Audited
    private String name;

    @Audited
    private Integer price;

    private Integer count;

    // Many 쪽에 Cascade.All 걸어놓으면 Many쪽 객체에 대한 영속성을 변경할 떄 One 쪽에도 함께 영향이 감
    // Ex) productItem을 삭제하는 경우 이 productItem과 연관돼있는 product까지 같이 삭제 됨
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // 파라미터가 하나인 경우 from을 사용하고, 여러 개인 경우 of를 사용한다
    public static ProductItem of(Long sellerId, AddProductItemForm form) {
        return ProductItem.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .price(form.getPrice())
                .count(form.getCount())
                .build();
    }
}
