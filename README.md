# Chess.NET

실시간 멀티플레이어 웹 체스 게임 플랫폼입니다. WebSocket을 활용한 실시간 게임 진행과 Spring Boot 기반의 견고한 백엔드 아키텍처를 제공합니다.

## 기술 스택

### Backend
- Java 21
- Spring Boot 3.3.4
    - Spring Security: 인증/인가 처리
    - Spring WebSocket: 실시간 게임 통신
    - Spring Data JPA: 데이터 영속성 관리
    - Spring Validation: 입력값 검증
- MySQL 8.0: 주 데이터베이스
- H2 Database: 테스트용 인메모리 데이터베이스
- Lombok: 보일러플레이트 코드 감소
- JaCoCo: 테스트 커버리지 분석 (최소 80% 커버리지 요구)

### Frontend
- React
- WebSocket (SockJS)
- STOMP

## 주요 기능

### 1. 실시간 게임 시스템
- WebSocket/STOMP 기반 양방향 실시간 통신
- 자동 재접속 및 게임 상태 복구
- 타임아웃 처리 및 연결 상태 모니터링
- 관전자 모드 지원

### 2. 체스 게임 로직
- 모든 기물 이동 규칙 구현
- 특수 이동 지원
    - 캐슬링 (킹사이드/퀸사이드)
    - 앙파상
    - 폰 프로모션
- 게임 상태 관리
    - 체크/체크메이트 감지
    - 스테일메이트 처리
    - 기권 기능
- 타이머 시스템
    - 커스텀 타임 컨트롤
    - 증가 시간(increment) 지원

### 3. 룸 시스템
- 동적 방 생성/참여
- 실시간 방 상태 동기화
- 방장 권한 관리
- 관전자 입장/퇴장 관리
- 게임 진행 중 재접속 지원

### 4. 보안 기능
- Spring Security 기반 사용자 인증
- CORS 설정을 통한 API 보안
- WebSocket 연결 보안
- 입력값 검증 및 sanitization

## 시스템 아키텍처

### WebSocket 통신 흐름
1. 클라이언트가 `/ws` 엔드포인트로 연결
2. STOMP를 통한 메시지 라우팅:
    - `/topic/game/{gameId}`: 게임 상태 구독
    - `/topic/rooms/{roomId}`: 방 상태 구독
    - `/queue/errors`: 개별 사용자 에러 처리
3. SockJS 폴백으로 브라우저 호환성 보장

### 체스 엔진 아키텍처
- 객체지향 설계로 각 기물 타입을 클래스로 모델링
- 체스보드 상태 관리와 이동 검증 로직 분리
- 이동 기록 및 게임 상태 저장

## 개발 환경 설정

### 필수 요구사항
- JDK 21
- MySQL 8.0
- Gradle 8.x

### 로컬 개발 환경 설정
```bash
# 데이터베이스 생성
mysql -u root -p
CREATE DATABASE chess_dot_net;

# 애플리케이션 설정
cp src/main/resources/application.properties.sample src/main/resources/application.properties
# application.properties 파일 수정

# 빌드 및 실행
./gradlew build
./gradlew bootRun
```

### 테스트
```bash
# 전체 테스트 실행
./gradlew test

# 테스트 커버리지 리포트 생성
./gradlew jacocoTestReport
# build/reports/jacoco/test/html/index.html 확인
```

## API 문서

### REST Endpoints
- 사용자 관리
    - `POST /api/users/create`: 새 사용자 생성
    - `GET /api/users/create-guest`: 게스트 사용자 생성
    - `GET /api/users/me`: 현재 사용자 정보 조회

- 방 관리
    - `POST /api/rooms/create`: 새 방 생성
    - `POST /api/rooms/join`: 방 참여
    - `POST /api/rooms/spectate`: 관전자로 참여
    - `GET /api/rooms/playable`: 참여 가능한 방 목록
    - `GET /api/rooms/spectatable`: 관전 가능한 방 목록

### WebSocket Topics
- `/topic/game/{gameId}`: 게임 상태 업데이트
- `/topic/rooms/{roomId}`: 방 상태 업데이트
- `/queue/errors`: 에러 메시지
- `/queue/game.state`: 개별 사용자 게임 상태

## 라이선스
MIT License

## 작성자
전종영