package com.greenbox.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateForm {

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    @Pattern(regexp = "^$|^01[0-9]-?\\d{3,4}-?\\d{4}$", message = "연락처 형식이 올바르지 않습니다.")
    private String phone;

    private String zipcode;
    private String address;
    private String addressDetail;

    /** 비밀번호 변경 시에만 입력 */
    private String newPassword;
}
