package com.example.chessdotnet.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JoinRoomRequest DTO 클래스에 대한 단위 테스트입니다.
 *
 * @author 전종영
 */
public class JoinRoomRequestTest {

    private Validator validator;

    /**
     * 각 테스트 메소드 실행 전에 Validator를 초기화합니다.
     */
    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 유효한 JoinRoomRequest 객체에 대한 검증을 테스트합니다.
     */
    @Test
    public void validJoinRoomRequest_ShouldPassValidation() {
        JoinRoomRequest request = new JoinRoomRequest();
        request.setUserId(1L);

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "유효한 요청은 검증을 통과해야 합니다.");
    }

    /**
     * userId가 null인 JoinRoomRequest 객체에 대한 검증을 테스트합니다.
     */
    @Test
    public void joinRoomRequestWithNullUserId_ShouldFailValidation() {
        JoinRoomRequest request = new JoinRoomRequest();
        request.setUserId(null);

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "null userId는 검증에 실패해야 합니다.");
        assertEquals(1, violations.size(), "하나의 위반 사항만 있어야 합니다.");
        assertEquals("사용자 ID는 필수입니다", violations.iterator().next().getMessage(),
                "올바른 오류 메시지가 반환되어야 합니다.");
    }
}