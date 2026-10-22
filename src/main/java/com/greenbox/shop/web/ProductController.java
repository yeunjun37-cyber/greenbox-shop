package com.greenbox.shop.web;

import com.greenbox.shop.domain.product.Difficulty;
import com.greenbox.shop.domain.product.Light;
import com.greenbox.shop.domain.product.Product;
import com.greenbox.shop.dto.ProductSearch;
import com.greenbox.shop.security.LoginMember;
import com.greenbox.shop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final WishService wishService;
    private final ReviewService reviewService;
    private final QnaService qnaService;

    /** F-07 목록 · F-08 맞춤 필터 · F-09 검색 */
    @GetMapping
    public String list(@ModelAttribute("search") ProductSearch search, Model model) {
        model.addAttribute("page", productService.search(search));
        model.addAttribute("categories", productService.categories());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("lights", Light.values());
        return "product/list";
    }

    /** F-10 상품 상세 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, @RequestParam(defaultValue = "guide") String tab,
                         @AuthenticationPrincipal LoginMember login, Model model) {
        Product product = productService.view(id);
        Long memberId = login == null ? null : login.getId();
        model.addAttribute("product", product);
        model.addAttribute("tab", tab);
        model.addAttribute("wished", wishService.isWished(memberId, id));
        model.addAttribute("reviews", reviewService.byProduct(id));
        model.addAttribute("rating", reviewService.average(id));
        model.addAttribute("qnas", qnaService.byProduct(id));
        model.addAttribute("loginId", memberId);
        model.addAttribute("isAdmin", login != null && login.isAdmin());
        return "product/detail";
    }
}
