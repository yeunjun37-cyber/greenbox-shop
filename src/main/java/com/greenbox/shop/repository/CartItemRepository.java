package com.greenbox.shop.repository;

import com.greenbox.shop.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select c from CartItem c join fetch c.product where c.member.id = :memberId order by c.id")
    List<CartItem> findWithProductByMemberId(Long memberId);

    Optional<CartItem> findByMemberIdAndProductId(Long memberId, Long productId);

    @Query("select coalesce(sum(c.quantity), 0) from CartItem c where c.member.id = :memberId")
    int sumQuantityByMemberId(Long memberId);
}
