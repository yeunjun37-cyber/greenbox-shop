package com.greenbox.shop.repository;

import com.greenbox.shop.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Member> findByEmailContainingOrNameContaining(String email, String name, Pageable pageable);

    long countByCreatedAtAfter(LocalDateTime from);
}
