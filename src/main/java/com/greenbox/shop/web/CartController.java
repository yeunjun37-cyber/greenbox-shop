package com.greenbox.shop.web;

import com.greenbox.shop.security.LoginMember;
import com.greenbox.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /** F-13 장바구니 조회 */
    @GetMapping
    public String list(@AuthenticationPrincipal LoginMember login, Model model) {
        model.addAttribute("cart", cartService.summary(login.getId()));
        return "cart/list";
    }

    /** F-12 장바구니 담기 (buyNow=true 이면 바로 장바구니로 이동) */
    @PostMapping
    public String add(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity,
                      @RequestParam(defaultValue = "false") boolean buyNow,
                      @AuthenticationPrincipal LoginMember login, RedirectAttributes ra) {
        cartService.add(login.getId(), productId, quantity);
        if (buyNow) return "redirect:/cart";
        ra.addFlashAttribute("message", "장바구니에 담았습니다.");
        return "redirect:/products/" + productId;
    }

    /** 수량 변경 */
    @PatchMapping("/{cartItemId}")
    public String changeQuantity(@PathVariable Long cartItemId, @RequestParam int quantity,
                                 @AuthenticationPrincipal LoginMember login) {
        cartService.changeQuantity(login.getId(), cartItemId, quantity);
        return "redirect:/cart";
    }

    /** 선택 삭제 */
    @DeleteMapping
    public String remove(@RequestParam(name = "cartItemIds", required = false) List<Long> ids,
                         @AuthenticationPrincipal LoginMember login, RedirectAttributes ra) {
        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "삭제할 상품을 선택하세요.");
        } else {
            cartService.remove(login.getId(), ids);
        }
        return "redirect:/cart";
    }
}
