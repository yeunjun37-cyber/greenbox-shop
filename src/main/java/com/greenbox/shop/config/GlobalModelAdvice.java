package com.greenbox.shop.config;

import com.greenbox.shop.repository.CartItemRepository;
import com.greenbox.shop.security.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** 모든 화면 헤더에 장바구니 수량 표시 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final CartItemRepository cartItemRepository;

    @ModelAttribute("cartCount")
    public int cartCount(@AuthenticationPrincipal LoginMember login) {
        return login == null ? 0 : cartItemRepository.sumQuantityByMemberId(login.getId());
    }
}
