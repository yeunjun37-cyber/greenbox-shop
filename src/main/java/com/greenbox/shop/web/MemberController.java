package com.greenbox.shop.web;

import com.greenbox.shop.dto.MemberJoinForm;
import com.greenbox.shop.exception.BusinessException;
import com.greenbox.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /** F-01 회원가입 */
    @GetMapping("/join")
    public String joinForm(@ModelAttribute("form") MemberJoinForm form) {
        return "member/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("form") MemberJoinForm form, BindingResult br, RedirectAttributes ra) {
        if (!br.hasFieldErrors("password") && !form.getPassword().equals(form.getPasswordConfirm())) {
            br.rejectValue("passwordConfirm", "mismatch", "비밀번호 확인이 일치하지 않습니다.");
        }
        if (br.hasErrors()) return "member/join";
        try {
            memberService.join(form);
        } catch (BusinessException e) {
            br.reject("join", e.getMessage());
            return "member/join";
        }
        ra.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해 주세요.");
        return "redirect:/member/login";
    }

    /** 이메일 중복 확인 (AJAX) */
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        return Map.of("available", memberService.isEmailAvailable(email));
    }

    /** F-02 로그인 화면 (처리는 Spring Security) */
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }
}
