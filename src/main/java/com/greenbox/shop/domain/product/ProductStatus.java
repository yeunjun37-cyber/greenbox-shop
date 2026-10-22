package com.greenbox.shop.domain.product;

public enum ProductStatus {
    SALE("판매중"), SOLDOUT("품절"), HIDDEN("숨김");

    private final String label;

    ProductStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
