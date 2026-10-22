package com.greenbox.shop.repository;

import com.greenbox.shop.domain.order.OrderItem;
import com.greenbox.shop.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /** 회원이 구매(취소 제외)한 식물 상품 목록 - 내 식물 등록용 */
    @Query("select distinct oi.product from OrderItem oi where oi.order.member.id = :memberId " +
            "and oi.order.status <> com.greenbox.shop.domain.order.OrderStatus.CANCELED and oi.product.difficulty is not null")
    List<Product> findPurchasedPlants(Long memberId);
}
