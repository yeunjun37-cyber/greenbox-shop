package com.greenbox.shop.security;

import com.greenbox.shop.domain.member.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** Spring Security 인증 사용자 정보 */
@Getter
public class LoginMember implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String name;
    private final String role;
    private final boolean enabled;

    public LoginMember(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.name = member.getName();
        this.role = member.getRole().name();
        this.enabled = !member.isDeleted();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
