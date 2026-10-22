package com.greenbox.shop.repository;

import com.greenbox.shop.domain.board.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductIdOrderByIdDesc(Long productId);

    List<Review> findByMemberIdOrderByIdDesc(Long memberId);

    boolean existsByOrderItemId(Long orderItemId);

    @Query("select avg(r.rating) from Review r where r.product.id = :productId")
    Double averageRating(Long productId);
}
