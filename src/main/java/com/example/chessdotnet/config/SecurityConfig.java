package com.example.chessdotnet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 애플리케이션의 보안 설정을 담당하는 설정 클래스입니다.
 *
 * @author 전종영
 */
@Configuration // 스프링 설정 클래스임을 나타냄
@EnableWebSecurity // 웹 보안 기능을 활성화
public class SecurityConfig {

    /**
     * 보안 필터 체인을 구성합니다.
     *
     * @author 전종영
     * @param http HttpSecurity 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 설정 중 발생할 수 있는 예외
     */
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