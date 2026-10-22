package com.greenbox.shop.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** 비즈니스 예외 발생 시 이전 화면으로 돌아가 메시지 표시 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException e, HttpServletRequest request, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", e.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}
