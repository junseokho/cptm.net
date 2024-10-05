package com.example.chessdotnet.config;

import com.example.chessdotnet.entity.User;
import com.example.chessdotnet.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 애플리케이션 시작 시 초기 데이터를 생성하는 설정 클래스입니다.
 */
@Configuration
public class DataInitializer {

    /**
     * 애플리케이션 시작 시 실행되어 테스트용 사용자 데이터를 생성합니다.
     *
     * @param userRepository 사용자 정보를 저장하는 리포지토리
     * @return 초기 데이터를 생성하는 CommandLineRunner
     */
    @Bean
    public CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            User user = new User();
            user.setUsername("testUser");
            userRepository.save(user);
            System.out.println("Test user created with ID: " + user.getId());
        };
    }
}