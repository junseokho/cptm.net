package com.example.chessdotnet.service;

import com.example.chessdotnet.entity.User;
import com.example.chessdotnet.exception.UserNotFoundException;
import com.example.chessdotnet.repository.UserRepository;
import com.example.chessdotnet.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * @author 전종영
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 새로운 비회원 사용자를 생성합니다.
     *
     * @return 생성된 사용자의 DTO
     */
    public UserDTO createGuestUser() {
        User user = new User();
        user.setUsername("Guest_" + System.currentTimeMillis());
        User savedUser = userRepository.save(user);
        return savedUser.toDTO();
    }

    /**
     * 새로운 사용자를 생성합니다.
     *
     * @param username 사용자 이름
     * @return 생성된 사용자의 DTO
     */
    public UserDTO createUser(String username) {
        User user = new User();
        user.setUsername(username);
        User savedUser = userRepository.save(user);
        return savedUser.toDTO();
    }

    /**
     * ID로 사용자를 조회합니다.
     *
     * @param id 사용자 ID
     * @return 조회된 사용자의 DTO
     * @throws UserNotFoundException 사용자를 찾을 수 없을 때 발생
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return user.toDTO();
    }


}
