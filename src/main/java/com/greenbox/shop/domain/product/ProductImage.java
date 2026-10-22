package com.greenbox.shop.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String imgUrl;

    private Integer sortOrder;

    static ProductImage create(Product product, String imgUrl, int sortOrder) {
        ProductImage i = new ProductImage();
        i.product = product;
        i.imgUrl = imgUrl;
        i.sortOrder = sortOrder;
        return i;
    }
}
