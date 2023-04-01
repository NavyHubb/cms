package com.zerobase.cms.order.domain.product;

import com.zerobase.cms.order.domain.model.Product;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private List<ProductItemDto> productItems;

    public static ProductDto from(Product product) {
        List<ProductItemDto> items = product.getProductItems().stream()
                .map(ProductItemDto::from)
                .collect(Collectors.toList());

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .productItems(items)
                .build();
    }

    public static ProductDto withoutItemsFrom(Product product) {
//        List<ProductItemDto> items = product.getProductItems().stream()
//                .map(ProductItemDto::from)
//                .collect(Collectors.toList());

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
//                .productItems(items)
                .build();
    }
}
