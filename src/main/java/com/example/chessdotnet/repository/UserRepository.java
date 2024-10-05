package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User 엔티티에 대한 데이터베이스 작업을 처리하는 리포지토리 인터페이스입니다.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository의 기본 CRUD 메서드를 상속받아 사용합니다.
}