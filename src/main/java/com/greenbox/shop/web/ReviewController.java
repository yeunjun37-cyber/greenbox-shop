package com.greenbox.shop.web;

import com.greenbox.shop.domain.order.OrderItem;
import com.greenbox.shop.exception.NotFoundException;
import com.greenbox.shop.repository.OrderItemRepository;
import com.greenbox.shop.security.LoginMember;
import com.greenbox.shop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final OrderItemRepository orderItemRepository;

    @GetMapping("/new")
    public String form(@RequestParam Long orderItemId, Model model) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("주문 상품을 찾을 수 없습니다."));
        model.addAttribute("item", item);
        model.addAttribute("menu", "reviews");
        return "mypage/review-form";
    }

    /** F-18 리뷰 작성 */
    @PostMapping
    public String write(@RequestParam Long orderItemId, @RequestParam int rating, @RequestParam String content,
                        @RequestParam(required = false) MultipartFile image,
                        @AuthenticationPrincipal LoginMember login, RedirectAttributes ra) {
        reviewService.write(login.getId(), orderItemId, rating, content, image);
        ra.addFlashAttribute("message", "리뷰가 등록되었습니다.");
        return "redirect:/mypage/reviews";
    }
}
