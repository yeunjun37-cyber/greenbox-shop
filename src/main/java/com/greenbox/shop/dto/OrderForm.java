package com.greenbox.shop.dto;

import com.greenbox.shop.domain.order.PaymentMethod;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderForm {

    @NotEmpty(message = "주문할 상품을 선택하세요.")
    private List<Long> cartItemIds = new ArrayList<>();

    @NotBlank(message = "받는 분을 입력하세요.")
    private String receiverName;

    @NotBlank(message = "연락처를 입력하세요.")
    private String receiverPhone;

    @NotBlank(message = "우편번호를 입력하세요.")
    private String zipcode;

    @NotBlank(message = "주소를 입력하세요.")
    private String address;

    private String addressDetail;
    private String memo;

    @NotNull
    private PaymentMethod paymentMethod = PaymentMethod.CARD;

    @AssertTrue(message = "주문 내용 확인 및 결제에 동의해 주세요.")
    private boolean agree;
}
