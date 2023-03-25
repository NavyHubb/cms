package com.zerobase.cms.order.domain.model;

import com.zerobase.cms.order.domain.product.AddProductForm;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited  // Envers로 엔티티의 이력 관리
@AuditOverride(forClass = BaseEntity.class)
public class Product extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private String name;

    private String description;

    // ProductItem의 product 컬럼을 주인으로 설정
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductItem> productItems = new ArrayList<>();

    public static Product of(Long sellerId, AddProductForm form) {
        return Product.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .description(form.getDescription())
                .productItems(form.getItems().stream()
                        .map(productItemForm -> ProductItem.of(sellerId, productItemForm)).collect(Collectors.toList()))
                .build();
    }

}