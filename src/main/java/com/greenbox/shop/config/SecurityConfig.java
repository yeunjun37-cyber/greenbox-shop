package com.greenbox.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** H2 콘솔 서블릿이 함께 등록되므로 Ant 패턴 매처를 명시적으로 사용 */
    private static RequestMatcher[] ant(String... patterns) {
        return Arrays.stream(patterns).map(AntPathRequestMatcher::antMatcher).toArray(RequestMatcher[]::new);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(ant("/css/**", "/js/**", "/img/**", "/uploads/**", "/favicon.ico", "/error")).permitAll()
                .requestMatchers(ant("/h2-console/**")).permitAll()
                .requestMatchers(ant("/", "/products", "/products/**", "/notices", "/notices/**",
                        "/member/join", "/member/login", "/member/check-email")).permitAll()
                .requestMatchers(ant("/admin", "/admin/**")).hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/member/login")
                .loginProcessingUrl("/member/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/", false)
                .failureUrl("/member/login?error")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout")
                .invalidateHttpSession(true))
            .csrf(csrf -> csrf.ignoringRequestMatchers(ant("/h2-console/**")))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()));
        return http.build();
    }
}
