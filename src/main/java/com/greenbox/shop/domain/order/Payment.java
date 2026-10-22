package com.greenbox.shop.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 모의 결제 정보 (주문과 1:1) */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PaymentMethod method;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    public static Payment create(Order order, PaymentMethod method) {
        Payment p = new Payment();
        p.order = order;
        p.method = method;
        p.amount = order.getTotalPrice();
        p.status = PaymentStatus.PAID;
        p.paidAt = LocalDateTime.now();
        return p;
    }

    void cancel() {
        this.status = PaymentStatus.CANCELED;
    }
}
