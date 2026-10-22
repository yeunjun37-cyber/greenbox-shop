package com.greenbox.shop.domain.order;

public enum OrderStatus {
    ORDERED("주문접수"), PAID("결제완료"), SHIPPING("배송중"), DELIVERED("배송완료"), CANCELED("주문취소");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
