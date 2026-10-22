package com.greenbox.shop.repository;

import com.greenbox.shop.domain.board.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAllByOrderByIdDesc(Pageable pageable);
}
