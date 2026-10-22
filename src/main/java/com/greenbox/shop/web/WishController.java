package com.greenbox.shop.web;

import com.greenbox.shop.security.LoginMember;
import com.greenbox.shop.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    /** F-11 찜 등록/해제 (AJAX 토글) */
    @PostMapping("/wish/{productId}")
    public Map<String, Boolean> toggle(@PathVariable Long productId, @AuthenticationPrincipal LoginMember login) {
        return Map.of("wished", wishService.toggle(login.getId(), productId));
    }
}
