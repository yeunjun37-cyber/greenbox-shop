package com.greenbox.shop.web;

import com.greenbox.shop.domain.member.Member;
import com.greenbox.shop.domain.order.Order;
import com.greenbox.shop.domain.order.OrderStatus;
import com.greenbox.shop.dto.MemberUpdateForm;
import com.greenbox.shop.exception.BusinessException;
import com.greenbox.shop.security.LoginMember;
import com.greenbox.shop.service.*;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final WishService wishService;
    private final ReviewService reviewService;

    @GetMapping
    public String index() {
        return "redirect:/mypage/orders";
    }

    /** F-16 주문 내역 */
    @GetMapping("/orders")
    public String orders(@AuthenticationPrincipal LoginMember login, Model model) {
        List<Order> orders = orderService.myOrders(login.getId());
        Map<OrderStatus, Long> counts = Arrays.stream(OrderStatus.values())
                .filter(s -> s != OrderStatus.ORDERED)
                .collect(Collectors.toMap(Function.identity(),
                        s -> orders.stream().filter(o -> o.getStatus() == s).count(),
                        (a, b) -> a, java.util.LinkedHashMap::new));
        model.addAttribute("orders", orders);
        model.addAttribute("counts", counts);
        model.addAttribute("menu", "orders");
        return "mypage/orders";
    }

    @GetMapping("/orders/{orderId}")
    public String orderDetail(@PathVariable Long orderId, @AuthenticationPrincipal LoginMember login, Model model) {
        Order order = orderService.getOwned(login.getId(), orderId);
        model.addAttribute("order", order);
        model.addAttribute("reviewed", order.getItems().stream()
                .collect(Collectors.toMap(i -> i.getId(), i -> reviewService.written(i.getId()))));
        model.addAttribute("menu", "orders");
        return "mypage/order-detail";
    }

    /** F-17 주문 취소 */
    @PostMapping("/orders/{orderId}/cancel")
    public String cancel(@PathVariable Long orderId, @AuthenticationPrincipal LoginMember login, RedirectAttributes ra) {
        orderService.cancel(login.getId(), orderId);
        ra.addFlashAttribute("message", "주문이 취소되었습니다. 재고가 복구되었습니다.");
        return "redirect:/mypage/orders";
    }

    @GetMapping("/wish")
    public String wish(@AuthenticationPrincipal LoginMember login, Model model) {
        model.addAttribute("wishes", wishService.list(login.getId()));
        model.addAttribute("menu", "wish");
        return "mypage/wish";
    }

    @GetMapping("/reviews")
    public String reviews(@AuthenticationPrincipal LoginMember login, Model model) {
        model.addAttribute("reviews", reviewService.byMember(login.getId()));
        model.addAttribute("menu", "reviews");
        return "mypage/reviews";
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
