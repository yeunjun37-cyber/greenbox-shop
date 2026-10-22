package com.greenbox.shop.domain.order;

public enum PaymentMethod {
    CARD("신용카드"), BANK("무통장입금");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
