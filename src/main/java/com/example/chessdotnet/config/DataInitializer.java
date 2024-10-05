package com.example.chessdotnet.config;

import com.example.chessdotnet.entity.User;
import com.example.chessdotnet.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

/**
 * 애플리케이션 초기화 시 초기 데이터를 생성하는 클래스.
 * 주로 데이터베이스에 기본 데이터가 없을 경우 테스트 사용자 데이터를 생성하는 역할을 수행합니다.
 */
@Configuration
public class DataInitializer {

    /**
     * 초기 데이터가 없을 경우 테스트 사용자를 생성하는 CommandLineRunner.
     * Spring Boot 애플리케이션이 실행될 때 자동으로 실행되며,
     * 데이터베이스에 사용자가 없을 경우 테스트 사용자("testUser")를 생성합니다.
     *
     * @author 전종영
     * @param userRepository 사용자 데이터를 저장할 UserRepository
     * @return CommandLineRunner로, 애플리케이션 시작 시 실행됩니다.
     */
    @Bean
    public CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
//            Long temp = userRepository.count();
//            Long temp2 = 0L;

            if (userRepository.count() == 0L) {  // 데이터베이스에 사용자가 없을 때만 실행
                User user = new User();
                user.setUsername("testUser");
                userRepository.save(user);
                System.out.println("username: " + user.getUsername()+ "\n" + "userId: " + user.getId());
            }
        };
    }
}
