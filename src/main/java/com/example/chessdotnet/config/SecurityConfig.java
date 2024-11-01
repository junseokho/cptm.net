package com.example.chessdotnet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

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
     * @param http HttpSecurity 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 기능 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**",                    // 웹소켓 엔드포인트
                                "/ws",                       // 웹소켓 기본 엔드포인트
                                "/topic/**",                 // STOMP 토픽 엔드포인트
                                "/api/rooms/available"       // 기존 허용된 엔드포인트
                        ).permitAll()
                        .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
                )
                .httpBasic(Customizer.withDefaults()); // 기본 HTTP 인증 사용
        return http.build();
    }

    /**
     * CORS 설정을 구성합니다.
     * WebSocket 연결을 위한 CORS 정책을 설정합니다.
     *
     * @return CorsConfigurationSource 구현체
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 모든 출처 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}