package com.greenbox.shop.web;

import com.greenbox.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    /** F-06 메인 화면 */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("newest", productService.newest());
        model.addAttribute("best", productService.best());
        return "index";
    }
}
