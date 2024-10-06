package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository 인터페이스에 대한 단위 테스트입니다.
 *
 * @author 전종영
 */
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    /**
     * 테스트용 User 엔티티를 생성합니다.
     *
     * @author 전종영
     * @param username 사용자 이름
     * @return 생성된 User 엔티티
     */
    private User createTestUser(String username) {
        User user = new User();
        user.setUsername(username);
        return entityManager.persist(user);
    }

    /**
     * 사용자 저장 및 ID로 조회 기능을 테스트합니다.
     *
     * @author 전종영
     */
    @Test
    void saveAndFindById_ShouldWorkCorrectly() {
        User savedUser = createTestUser("testUser");

        entityManager.flush();

        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testUser");
    }

    /**
     * 모든 사용자를 조회하는 기능을 테스트합니다.
     *
     * @author 전종영
     */
    @Test
    void findAll_ShouldReturnAllUsers() {
        createTestUser("user1");
        createTestUser("user2");
        createTestUser("user3");

        entityManager.flush();

        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers).hasSize(3)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("user1", "user2", "user3");
    }

    /**
     * 사용자 삭제 기능을 테스트합니다.
     *
     * @author 전종영
     */
    @Test
    void deleteUser_ShouldRemoveUserFromDatabase() {
        User user = createTestUser("userToDelete");

        entityManager.flush();

        userRepository.delete(user);

        User deletedUser = userRepository.findById(user.getId()).orElse(null);

        assertThat(deletedUser).isNull();
    }

    /**
     * 사용자 업데이트 기능을 테스트합니다.
     *
     * @author 전종영
     */
    @Test
    void updateUser_ShouldChangeUserProperties() {
        User user = createTestUser("originalUsername");

        entityManager.flush();

        user.setUsername("updatedUsername");
        userRepository.save(user);

        User updatedUser = userRepository.findById(user.getId()).orElse(null);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getUsername()).isEqualTo("updatedUsername");
    }
}