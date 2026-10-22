package com.greenbox.shop.web;

import com.greenbox.shop.domain.product.Difficulty;
import com.greenbox.shop.domain.product.Light;
import com.greenbox.shop.dto.ProductSearch;
import com.greenbox.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /** F-07 목록 · F-08 맞춤 필터 · F-09 검색 */
    @GetMapping
    public String list(@ModelAttribute("search") ProductSearch search, Model model) {
        model.addAttribute("page", productService.search(search));
        model.addAttribute("categories", productService.categories());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("lights", Light.values());
        return "product/list";
    }
}
