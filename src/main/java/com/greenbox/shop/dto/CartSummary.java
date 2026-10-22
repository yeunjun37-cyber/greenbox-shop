package com.greenbox.shop.dto;

import com.greenbox.shop.domain.cart.CartItem;

import java.util.List;

/** 장바구니 · 주문서 금액 계산 (3만원 이상 무료배송) */
public record CartSummary(List<CartItem> items, int productTotal, int shippingFee, int total) {

    public static final int FREE_SHIPPING = 30_000;
    public static final int SHIPPING_FEE = 3_000;

    public static CartSummary of(List<CartItem> items) {
        int sum = items.stream().mapToInt(CartItem::getTotalPrice).sum();
        int fee = sum == 0 || sum >= FREE_SHIPPING ? 0 : SHIPPING_FEE;
        return new CartSummary(items, sum, fee, sum + fee);
    }
}
