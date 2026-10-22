package com.greenbox.shop.repository;

import com.greenbox.shop.domain.board.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {
    List<Qna> findByProductIdOrderByIdDesc(Long productId);

    Page<Qna> findAllByOrderByIdDesc(Pageable pageable);

    long countByAnswerIsNull();
}
