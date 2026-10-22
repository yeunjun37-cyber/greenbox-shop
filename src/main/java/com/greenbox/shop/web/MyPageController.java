package com.greenbox.shop.web;

import com.greenbox.shop.domain.member.Member;
import com.greenbox.shop.dto.MemberUpdateForm;
import com.greenbox.shop.exception.BusinessException;
import com.greenbox.shop.security.LoginMember;
import com.greenbox.shop.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;

    @GetMapping
    public String index() {
        return "redirect:/mypage/info";
    }

    /** F-04 회원정보 수정 */
    @GetMapping("/info")
    public String infoForm(@AuthenticationPrincipal LoginMember login, Model model) {
        Member m = memberService.get(login.getId());
        MemberUpdateForm form = new MemberUpdateForm();
        form.setName(m.getName());
        form.setPhone(m.getPhone());
        form.setZipcode(m.getZipcode());
        form.setAddress(m.getAddress());
        form.setAddressDetail(m.getAddressDetail());
        model.addAttribute("form", form);
        model.addAttribute("email", m.getEmail());
        model.addAttribute("menu", "info");
        return "mypage/info";
    }

    @PostMapping("/info")
    public String update(@Valid @ModelAttribute("form") MemberUpdateForm form, BindingResult br,
                         @AuthenticationPrincipal LoginMember login, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("email", login.getEmail());
            model.addAttribute("menu", "info");
            return "mypage/info";
        }
        memberService.update(login.getId(), form);
        ra.addFlashAttribute("message", "회원정보가 수정되었습니다.");
        return "redirect:/mypage/info";
    }

    /** F-05 회원 탈퇴 */
    @PostMapping("/withdraw")
    public String withdraw(@RequestParam String password, @AuthenticationPrincipal LoginMember login,
                           HttpServletRequest request, RedirectAttributes ra) throws ServletException {
        try {
            memberService.withdraw(login.getId(), password);
        } catch (BusinessException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/mypage/info";
        }
        request.logout();
        ra.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다. 그동안 이용해 주셔서 감사합니다.");
        return "redirect:/";
    }
}
