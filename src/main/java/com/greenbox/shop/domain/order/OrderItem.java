package com.greenbox.shop.domain.order;

import com.greenbox.shop.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** 주문 당시 단가 */
    @Column(nullable = false)
    private int orderPrice;

    @Column(nullable = false)
    private int quantity;

    /** 상품 재고를 차감하면서 주문 상품 생성 */
    public static OrderItem create(Product product, int quantity) {
        product.removeStock(quantity);
        OrderItem i = new OrderItem();
        i.product = product;
        i.orderPrice = product.getPrice();
        i.quantity = quantity;
        return i;
    }

    void setOrder(Order order) {
        this.order = order;
    }

    void restoreStock() {
        product.addStock(quantity);
    }

    public int getTotalPrice() {
        return orderPrice * quantity;
    }
}
