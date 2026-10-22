package com.greenbox.shop.dto;

import com.greenbox.shop.domain.product.Difficulty;
import com.greenbox.shop.domain.product.Light;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** 상품 목록 검색 · 필터 조건 */
@Getter
@Setter
public class ProductSearch {
    private Long categoryId;
    private String keyword;
    private List<Difficulty> difficulty = new ArrayList<>();
    private List<Light> light = new ArrayList<>();
    private boolean petSafe;
    /** new, low, high, popular */
    private String sort = "new";
    private int page = 0;
}
