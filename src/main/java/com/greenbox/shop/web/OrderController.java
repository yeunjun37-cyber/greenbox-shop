package com.greenbox.shop.web;

import com.greenbox.shop.domain.member.Member;
import com.greenbox.shop.domain.order.PaymentMethod;
import com.greenbox.shop.dto.OrderForm;
import com.greenbox.shop.security.LoginMember;
import com.greenbox.shop.service.CartService;
import com.greenbox.shop.service.MemberService;
import com.greenbox.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final MemberService memberService;

    /** F-14 주문서 작성 */
    @GetMapping("/form")
    public String form(@RequestParam(name = "cartItemIds", required = false) List<Long> ids,
                       @AuthenticationPrincipal LoginMember login, Model model, RedirectAttributes ra) {
        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "주문할 상품을 선택하세요.");
            return "redirect:/cart";
        }
        OrderForm form = new OrderForm();
        form.setCartItemIds(ids);
        return render(form, login, model);
    }

    /** F-15 결제 처리 */
    @PostMapping
    public String order(@Valid @ModelAttribute("form") OrderForm form, BindingResult br,
                        @AuthenticationPrincipal LoginMember login, Model model) {
        if (br.hasErrors()) {
            return render(form, login, model);
        }
        Long orderId = orderService.order(login.getId(), form);
        return "redirect:/order/" + orderId + "/done";
    }

    @GetMapping("/{orderId}/done")
    public String done(@PathVariable Long orderId, @AuthenticationPrincipal LoginMember login, Model model) {
        model.addAttribute("order", orderService.getOwned(login.getId(), orderId));
        return "order/done";
    }

    private String render(OrderForm form, LoginMember login, Model model) {
        Member member = memberService.get(login.getId());
        model.addAttribute("form", form);
        model.addAttribute("cart", cartService.summary(login.getId(), form.getCartItemIds()));
        model.addAttribute("member", member);
        model.addAttribute("methods", PaymentMethod.values());
        return "order/form";
    }
}
