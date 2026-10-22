package com.greenbox.shop.domain.plant;

import com.greenbox.shop.domain.member.Member;
import com.greenbox.shop.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/** 내 식물: 구매한 식물의 물주기 일정 관리 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPlant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_plant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 30)
    private String nickname;

    /** 물주기 주기(일) - 기본값은 상품의 권장 주기, 사용자가 수정 가능 */
    @Column(nullable = false)
    private int waterCycle;

    private LocalDate lastWateredAt;

    private LocalDate nextWaterAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static MyPlant create(Member member, Product product, String nickname, Integer waterCycle, LocalDate lastWateredAt) {
        MyPlant p = new MyPlant();
        p.member = member;
        p.product = product;
        p.nickname = nickname;
        int cycle = waterCycle != null && waterCycle > 0 ? waterCycle
                : (product.getWaterCycle() != null ? product.getWaterCycle() : 7);
        p.waterCycle = cycle;
        p.water(lastWateredAt != null ? lastWateredAt : LocalDate.now());
        return p;
    }

    /** 물 준 날 기록 → 다음 물주기 날짜 재계산 */
    public void water(LocalDate date) {
        this.lastWateredAt = date;
        this.nextWaterAt = date.plusDays(waterCycle);
    }

    public void changeCycle(int waterCycle) {
        this.waterCycle = Math.max(1, waterCycle);
        if (lastWateredAt != null) {
            this.nextWaterAt = lastWateredAt.plusDays(this.waterCycle);
        }
    }

    /** 다음 물주기까지 남은 일수 (음수면 지남) */
    public long getDaysLeft() {
        return ChronoUnit.DAYS.between(LocalDate.now(), nextWaterAt);
    }

    /** 진행 막대 표시용(0~100) */
    public int getProgress() {
        long passed = waterCycle - getDaysLeft();
        return (int) Math.max(5, Math.min(100, passed * 100 / waterCycle));
    }

    public boolean isDue() {
        return getDaysLeft() <= 0;
    }
}
