package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // 스프링의 데이터 접근 계층 컴포넌트임을 나타냄
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository의 기본 CRUD 메서드만 사용
}