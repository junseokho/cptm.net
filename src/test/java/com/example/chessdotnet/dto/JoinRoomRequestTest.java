package com.example.chessdotnet.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JoinRoomRequest DTO 클래스에 대한 단위 테스트입니다.
 */
public class JoinRoomRequestTest {

    private Validator validator;

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
        assertTrue(violations.isEmpty());
    }

    /**
     * userId가 null인 JoinRoomRequest 객체에 대한 검증을 테스트합니다.
     */
    @Test
    public void joinRoomRequestWithNullUserId_ShouldFailValidation() {
        JoinRoomRequest request = new JoinRoomRequest();
        request.setUserId(null);

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("사용자 ID는 필수입니다", violations.iterator().next().getMessage());
    }
}