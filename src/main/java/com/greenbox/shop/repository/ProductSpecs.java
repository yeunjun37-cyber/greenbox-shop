package com.greenbox.shop.repository;

import com.greenbox.shop.domain.product.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

/** 상품 목록 · 식물 맞춤 필터 조건 */
public final class ProductSpecs {

    private ProductSpecs() {
    }

    public static Specification<Product> visible() {
        return (root, q, cb) -> cb.notEqual(root.get("status"), ProductStatus.HIDDEN);
    }

    public static Specification<Product> category(Long categoryId) {
        return (root, q, cb) -> categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> keyword(String keyword) {
        return (root, q, cb) -> keyword == null || keyword.isBlank() ? null
                : cb.like(root.get("name"), "%" + keyword.trim() + "%");
    }

    public static Specification<Product> difficultyIn(Collection<Difficulty> values) {
        return (root, q, cb) -> values == null || values.isEmpty() ? null : root.get("difficulty").in(values);
    }

    public static Specification<Product> lightIn(Collection<Light> values) {
        return (root, q, cb) -> values == null || values.isEmpty() ? null : root.get("light").in(values);
    }

    public static Specification<Product> petSafeOnly(boolean petSafe) {
        return (root, q, cb) -> petSafe ? cb.equal(root.get("petSafe"), "Y") : null;
    }
}
