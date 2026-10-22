package com.greenbox.shop.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(length = 10)
    private String zipcode;

    private String address;

    private String addressDetail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, length = 1)
    private String deletedYn = "N";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static Member create(String email, String encodedPassword, String name, String phone,
                                String zipcode, String address, String addressDetail, Role role) {
        Member m = new Member();
        m.email = email;
        m.password = encodedPassword;
        m.name = name;
        m.phone = phone;
        m.zipcode = zipcode;
        m.address = address;
        m.addressDetail = addressDetail;
        m.role = role;
        return m;
    }

    public void updateInfo(String name, String phone, String zipcode, String address, String addressDetail) {
        this.name = name;
        this.phone = phone;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /** 회원 탈퇴 (논리 삭제) */
    public void withdraw() {
        this.deletedYn = "Y";
    }

    public boolean isDeleted() {
        return "Y".equals(deletedYn);
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
