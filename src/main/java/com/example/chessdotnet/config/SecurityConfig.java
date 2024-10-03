package com.example.chessdotnet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 스프링 설정 클래스임을 나타냄
@EnableWebSecurity // 웹 보안 기능을 활성화
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 기능 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/rooms/available").permitAll() // "/api/rooms/available" 엔드포인트는 모든 사용자에게 허용
                        .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
                )
                .httpBasic(Customizer.withDefaults()); // 기본 HTTP 인증 사용
        return http.build();
    }
}