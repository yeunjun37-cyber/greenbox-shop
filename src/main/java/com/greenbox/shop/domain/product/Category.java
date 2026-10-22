package com.greenbox.shop.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    /** 상위 카테고리 (자기참조) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    private Integer sortOrder;

    public static Category create(String name, Category parent, int sortOrder) {
        Category c = new Category();
        c.name = name;
        c.parent = parent;
        c.sortOrder = sortOrder;
        return c;
    }
}
