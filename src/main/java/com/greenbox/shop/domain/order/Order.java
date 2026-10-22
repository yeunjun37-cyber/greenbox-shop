package com.greenbox.shop.domain.order;

import com.greenbox.shop.domain.member.Member;
import com.greenbox.shop.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false, length = 30)
    private String receiverName;

    @Column(nullable = false, length = 20)
    private String receiverPhone;

    @Column(nullable = false, length = 10)
    private String zipcode;

    @Column(nullable = false)
    private String address;

    @Column(length = 200)
    private String memo;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    public static Order create(String orderNo, Member member, String receiverName, String receiverPhone,
                               String zipcode, String address, String memo) {
        Order o = new Order();
        o.orderNo = orderNo;
        o.member = member;
        o.receiverName = receiverName;
        o.receiverPhone = receiverPhone;
        o.zipcode = zipcode;
        o.address = address;
        o.memo = memo;
        o.status = OrderStatus.ORDERED;
        o.orderedAt = LocalDateTime.now();
        return o;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        totalPrice += item.getTotalPrice();
    }

    public void addShippingFee(int fee) {
        totalPrice += fee;
    }

    /** 결제 완료 처리 */
    public void pay(Payment payment) {
        this.payment = payment;
        this.status = OrderStatus.PAID;
    }

    /** 주문 취소: 결제완료 상태에서만 가능, 재고 복구 */
    public void cancel() {
        if (status != OrderStatus.PAID) {
            throw new BusinessException("결제완료 상태의 주문만 취소할 수 있습니다.");
        }
        status = OrderStatus.CANCELED;
        items.forEach(OrderItem::restoreStock);
        if (payment != null) {
            payment.cancel();
        }
    }

    /** 관리자 배송 상태 변경 */
    public void changeStatus(OrderStatus newStatus) {
        if (status == OrderStatus.CANCELED) {
            throw new BusinessException("취소된 주문은 상태를 변경할 수 없습니다.");
        }
        if (newStatus == OrderStatus.CANCELED) {
            cancel();
            return;
        }
        this.status = newStatus;
    }

    public String getSummaryName() {
        if (items.isEmpty()) return "";
        String first = items.get(0).getProduct().getName();
        return items.size() > 1 ? first + " 외 " + (items.size() - 1) + "건" : first;
    }

    public boolean isCancelable() {
        return status == OrderStatus.PAID;
    }
}
