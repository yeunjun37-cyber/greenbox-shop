package com.greenbox.shop.web;

import com.greenbox.shop.security.LoginMember;
import com.greenbox.shop.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    /** F-19 상품 문의 등록 */
    @PostMapping("/qna")
    public String ask(@RequestParam Long productId, @RequestParam String title, @RequestParam String content,
                      @RequestParam(defaultValue = "false") boolean secret,
                      @AuthenticationPrincipal LoginMember login, RedirectAttributes ra) {
        qnaService.ask(login.getId(), productId, title, content, secret);
        ra.addFlashAttribute("message", "문의가 등록되었습니다.");
        return "redirect:/products/" + productId + "?tab=qna";
    }
}
