package com.greenbox.shop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberJoinForm {

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자로 입력하세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력하세요.")
    private String passwordConfirm;

    @NotBlank(message = "이름을 입력하세요.")
    @Size(max = 30)
    private String name;

    @Pattern(regexp = "^$|^01[0-9]-?\\d{3,4}-?\\d{4}$", message = "연락처 형식이 올바르지 않습니다.")
    private String phone;

    private String zipcode;
    private String address;
    private String addressDetail;
}
