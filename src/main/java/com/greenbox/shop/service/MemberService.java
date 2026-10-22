package com.greenbox.shop.service;

import com.greenbox.shop.domain.member.Member;
import com.greenbox.shop.domain.member.Role;
import com.greenbox.shop.dto.MemberJoinForm;
import com.greenbox.shop.dto.MemberUpdateForm;
import com.greenbox.shop.exception.BusinessException;
import com.greenbox.shop.exception.NotFoundException;
import com.greenbox.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /** F-01 회원가입 */
    @Transactional
    public Long join(MemberJoinForm form) {
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            throw new BusinessException("비밀번호 확인이 일치하지 않습니다.");
        }
        if (memberRepository.existsByEmail(form.getEmail())) {
            throw new BusinessException("이미 가입된 이메일입니다.");
        }
        Member member = Member.create(form.getEmail(), passwordEncoder.encode(form.getPassword()), form.getName(),
                form.getPhone(), form.getZipcode(), form.getAddress(), form.getAddressDetail(), Role.USER);
        return memberRepository.save(member).getId();
    }

    public boolean isEmailAvailable(String email) {
        return !memberRepository.existsByEmail(email);
    }

    public Member get(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
    }

    /** F-04 회원정보 수정 */
    @Transactional
    public void update(Long memberId, MemberUpdateForm form) {
        Member member = get(memberId);
        member.updateInfo(form.getName(), form.getPhone(), form.getZipcode(), form.getAddress(), form.getAddressDetail());
        if (form.getNewPassword() != null && !form.getNewPassword().isBlank()) {
            if (form.getNewPassword().length() < 8) {
                throw new BusinessException("새 비밀번호는 8자 이상이어야 합니다.");
            }
            member.changePassword(passwordEncoder.encode(form.getNewPassword()));
        }
    }

    /** F-05 회원 탈퇴 (비밀번호 재확인) */
    @Transactional
    public void withdraw(Long memberId, String rawPassword) {
        Member member = get(memberId);
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new BusinessException("비밀번호가 일치하지 않습니다.");
        }
        member.withdraw();
    }

    public Page<Member> search(String keyword, int page) {
        PageRequest pr = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        if (keyword == null || keyword.isBlank()) return memberRepository.findAll(pr);
        return memberRepository.findByEmailContainingOrNameContaining(keyword, keyword, pr);
    }
}
