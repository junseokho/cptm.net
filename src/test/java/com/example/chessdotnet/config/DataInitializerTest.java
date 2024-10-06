package com.example.chessdotnet.config;

import com.example.chessdotnet.entity.User;
import com.example.chessdotnet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DataInitializer 클래스의 기능을 테스트하는 클래스입니다.
 * 이 테스트는 초기 데이터 생성 로직이 올바르게 동작하는지 확인합니다.
 *
 * @author 전종영
 */
@SpringBootTest
public class DataInitializerTest {

    @Autowired
    private DataInitializer dataInitializer;

    @MockBean
    private UserRepository userRepository;

    /**
     * 데이터베이스가 비어있을 때 initData 메서드가 테스트 사용자를 올바르게 생성하는지 검증하는 테스트.
     *
     * @throws Exception 테스트 실행 중 예외가 발생할 경우
     * @author 전종영
     */
    @Test
    void givenEmptyDatabase_whenInitData_thenShouldCreateTestUser() throws Exception {
        when(userRepository.count()).thenReturn(0L);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        dataInitializer.initData(userRepository).run(new String[]{});

        verify(userRepository, times(1)).count();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("testUser", capturedUser.getUsername());
    }

    /**
     * 데이터베이스에 이미 사용자가 존재할 때 initData 메서드가 새 사용자를 생성하지 않는지 검증하는 테스트.
     *
     * @throws Exception 테스트 실행 중 예외가 발생할 경우
     * @author 전종영
     */
    @Test
    void givenNonEmptyDatabase_whenInitData_thenShouldNotCreateTestUser() throws Exception {
        when(userRepository.count()).thenReturn(1L);

        dataInitializer.initData(userRepository).run(new String[]{});

        verify(userRepository, times(1)).count();
        verify(userRepository, never()).save(any(User.class));
    }
}