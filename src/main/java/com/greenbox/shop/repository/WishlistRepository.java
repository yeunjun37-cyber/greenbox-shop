package com.greenbox.shop.repository;

import com.greenbox.shop.domain.product.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByMemberIdAndProductId(Long memberId, Long productId);

    List<Wishlist> findByMemberIdOrderByIdDesc(Long memberId);

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}
