package com.greenbox.shop.domain.board;

import com.greenbox.shop.domain.member.Member;
import com.greenbox.shop.domain.order.OrderItem;
import com.greenbox.shop.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** 주문 상품 1건당 리뷰 1개 */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false, unique = true)
    private OrderItem orderItem;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String imgUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Review create(Member member, OrderItem orderItem, int rating, String content, String imgUrl) {
        Review r = new Review();
        r.member = member;
        r.orderItem = orderItem;
        r.product = orderItem.getProduct();
        r.rating = Math.max(1, Math.min(5, rating));
        r.content = content;
        r.imgUrl = imgUrl;
        return r;
    }
}
