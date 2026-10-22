package com.greenbox.shop.domain.product;

public enum Light {
    LOW("음지 OK"), MID("반음지"), HIGH("양지");

    private final String label;

    Light(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
