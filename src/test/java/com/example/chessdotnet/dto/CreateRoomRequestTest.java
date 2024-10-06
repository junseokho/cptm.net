package com.example.chessdotnet.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CreateRoomRequest DTO 클래스에 대한 단위 테스트입니다.
 *
 * @author 전종영
 */
public class CreateRoomRequestTest {

    private Validator validator;

    /**
     * 각 테스트 메소드 실행 전에 Validator를 초기화합니다.
     *
     * @author 전종영
     */
    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 유효한 CreateRoomRequest 객체에 대한 검증을 테스트합니다.
     *
     * @author 전종영
     */
    @Test
    public void validCreateRoomRequest_ShouldPassValidation() {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setTitle("Valid Title");
        request.setCreatorId(1L);

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "유효한 요청은 검증을 통과해야 합니다.");
    }

    /**
     * 다양한 무효한 제목에 대한 CreateRoomRequest 객체 검증을 테스트합니다.
     *
     * @author 전종영
     * @param title 테스트할 제목
     */
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "ab"})
    public void createRoomRequestWithInvalidTitle_ShouldFailValidation(String title) {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setTitle(title);
        request.setCreatorId(1L);

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "무효한 제목은 검증에 실패해야 합니다.");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("방 제목은")),
                "제목 관련 오류 메시지가 있어야 합니다.");
    }

    /**
     * creatorId가 null인 CreateRoomRequest 객체에 대한 검증을 테스트합니다.
     *
     * @author 전종영
     */
    @Test
    public void createRoomRequestWithNullCreatorId_ShouldFailValidation() {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setTitle("Valid Title");
        request.setCreatorId(null);

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "null creatorId는 검증에 실패해야 합니다.");
        assertEquals(1, violations.size(), "하나의 위반 사항만 있어야 합니다.");
        assertEquals("방장 ID는 필수입니다", violations.iterator().next().getMessage(),
                "올바른 오류 메시지가 반환되어야 합니다.");
    }
}