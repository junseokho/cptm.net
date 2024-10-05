package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository 인터페이스에 대한 단위 테스트입니다.
 */
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    /**
     * 사용자 저장 및 조회 기능을 테스트합니다.
     */
    @Test
    void saveAndFindUser_ShouldWorkCorrectly() {
        User user = new User();
        user.setUsername("testUser");

        entityManager.persist(user);
        entityManager.flush();

        User found = userRepository.findById(user.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("testUser");
    }
}