package com.greenbox.shop.web.admin;

import com.greenbox.shop.domain.order.OrderStatus;
import com.greenbox.shop.domain.product.Difficulty;
import com.greenbox.shop.domain.product.Light;
import com.greenbox.shop.dto.ProductForm;
import com.greenbox.shop.security.LoginMember;
import com.greenbox.shop.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/** 관리자 기능 F-23 ~ F-27 (/admin/** 는 ADMIN 권한만 접근) */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final MemberService memberService;
    private final QnaService qnaService;
    private final NoticeService noticeService;
    private final com.greenbox.shop.repository.MemberRepository memberRepository;

    /** F-23 대시보드 */
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("todayOrders", orderService.todayCount());
        model.addAttribute("todaySales", orderService.todaySales());
        model.addAttribute("newMembers", memberRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay()));
        model.addAttribute("unanswered", qnaService.unansweredCount());
        model.addAttribute("lowStock", productService.lowStock());
        model.addAttribute("recent", orderService.recent());
        model.addAttribute("menu", "dash");
        return "admin/dashboard";
    }

    // ---------- F-24 상품 관리 ----------
    @GetMapping("/products")
    public String products(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", productService.adminList(keyword, page));
        model.addAttribute("keyword", keyword);
        model.addAttribute("menu", "products");
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        return productForm(new ProductForm(), model);
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        return productForm(ProductForm.from(productService.get(id)), model);
    }

    @PostMapping("/products")
    public String saveProduct(@Valid @ModelAttribute("form") ProductForm form, BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) return productForm(form, model);
        productService.save(form);
        ra.addFlashAttribute("message", form.getId() == null ? "상품이 등록되었습니다." : "상품이 수정되었습니다.");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        productService.hide(id);
        ra.addFlashAttribute("message", "상품을 숨김 처리했습니다. (주문 이력 보존)");
        return "redirect:/admin/products";
    }

    private String productForm(ProductForm form, Model model) {
        model.addAttribute("form", form);
        model.addAttribute("categories", productService.categories());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("lights", Light.values());
        model.addAttribute("menu", "products");
        return "admin/product-form";
    }

    // ---------- F-25 주문 관리 ----------
    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) OrderStatus status, @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", orderService.adminList(status, page));
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("status", status);
        model.addAttribute("menu", "orders");
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String changeOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status, RedirectAttributes ra) {
        orderService.changeStatus(id, status);
        ra.addFlashAttribute("message", "주문 상태를 ‘" + status.getLabel() + "’(으)로 변경했습니다.");
        return "redirect:/admin/orders";
    }

    // ---------- F-26 회원 관리 ----------
    @GetMapping("/members")
    public String members(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", memberService.search(keyword, page));
        model.addAttribute("keyword", keyword);
        model.addAttribute("menu", "members");
        return "admin/members";
    }

    // ---------- F-27 게시판 관리 ----------
    @GetMapping("/qna")
    public String qna(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", qnaService.adminList(page));
        model.addAttribute("menu", "qna");
        return "admin/qna";
    }

    @PostMapping("/qna/{id}/answer")
    public String answer(@PathVariable Long id, @RequestParam String answer, RedirectAttributes ra) {
        qnaService.answer(id, answer);
        ra.addFlashAttribute("message", "답변을 등록했습니다.");
        return "redirect:/admin/qna";
    }

    @GetMapping("/notices")
    public String notices(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", noticeService.list(page));
        model.addAttribute("menu", "notices");
        return "admin/notices";
    }

    @GetMapping({"/notices/new", "/notices/{id}/edit"})
    public String noticeForm(@PathVariable(required = false) Long id, Model model) {
        model.addAttribute("notice", id == null ? null : noticeService.get(id));
        model.addAttribute("menu", "notices");
        return "admin/notice-form";
    }

    @PostMapping("/notices")
    public String saveNotice(@RequestParam(required = false) Long id, @RequestParam String title, @RequestParam String content,
                             @AuthenticationPrincipal LoginMember login, RedirectAttributes ra) {
        noticeService.save(login.getId(), id, title, content);
        ra.addFlashAttribute("message", "공지사항을 저장했습니다.");
        return "redirect:/admin/notices";
    }

    @PostMapping("/notices/{id}/delete")
    public String deleteNotice(@PathVariable Long id, RedirectAttributes ra) {
        noticeService.delete(id);
        ra.addFlashAttribute("message", "공지사항을 삭제했습니다.");
        return "redirect:/admin/notices";
    }
}
