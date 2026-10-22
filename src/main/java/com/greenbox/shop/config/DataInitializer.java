package com.greenbox.shop.config;

import com.greenbox.shop.domain.member.Member;
import com.greenbox.shop.domain.member.Role;
import com.greenbox.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 최초 실행 시 기본 데이터 생성 (DB가 비어 있을 때만)
 * - 관리자: admin@greenbox.com / admin1234
 * - 회원:   chorok@greenbox.com / greenbox1234
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (memberRepository.count() > 0) return;

        Member admin = memberRepository.save(Member.create("admin@greenbox.com", passwordEncoder.encode("admin1234"),
                "관리자", "010-0000-0000", null, null, null, Role.ADMIN));
        Member user = memberRepository.save(Member.create("chorok@greenbox.com", passwordEncoder.encode("greenbox1234"),
                "김초록", "010-1234-5678", "22004", "인천광역시 연수구 송도과학로 123", "101동 1004호", Role.USER));

        log.info("기본 데이터 생성 완료 - 관리자 admin@greenbox.com / admin1234, 회원 chorok@greenbox.com / greenbox1234");
    }
}
