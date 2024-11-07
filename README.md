# Chess.NET (소프트웨어 공학)

Chess.NET은 실시간 멀티플레이어 웹 체스 게임 플랫폼입니다.

## 기술 스택

### Backend
- Java 21
- Spring Boot 3.3.4
- Spring Security
- Spring WebSocket
- Spring Data JPA
- MySQL 8.0
- Lombok
- JaCoCo (테스트 커버리지)

### Frontend
- React
- WebSocket (SockJS)
- STOMP

## 주요 기능

### 1. 실시간 게임 시스템
- WebSocket을 활용한 실시간 양방향 통신
- STOMP 프로토콜 기반의 메시지 교환
- 실시간 게임 상태 동기화

### 2. 룸 시스템
- 방 생성 및 참여
- 실시간 방 상태 업데이트
- 방장 권한 관리
- 최대 2인 플레이어 제한

### 3. 체스 게임 로직
- 기물 이동 유효성 검증
- 특수 이동 지원 (캐슬링, 앙파상, 프로모션)
- 게임 상태 관리 (체크, 체크메이트, 스테일메이트)

### 4. 사용자 관리
- 게스트 사용자 지원
- 세션 기반 사용자 관리
- 사용자별 게임 이력 관리

## 시스템 아키텍처

### 1. 계층 구조
- Controller Layer: REST API 및 WebSocket 엔드포인트 처리
- Service Layer: 비즈니스 로직 처리
- Repository Layer: 데이터 접근 계층
- Entity Layer: 도메인 모델

### 2. 웹소켓 아키텍처
- STOMP 메시지 브로커 사용
- SockJS를 통한 WebSocket 폴백 지원
- 실시간 이벤트 처리 시스템

### 3. 데이터베이스 구조
- Room: 게임방 정보 관리
- User: 사용자 정보 관리
- ChessGame: 게임 상태 및 진행 정보 관리

## 보안 기능
- Spring Security 기반 인증
- CORS 설정을 통한 크로스 도메인 요청 관리
- WebSocket 연결 보안

## 개발 환경 설정

### 필수 요구사항
- JDK 21
- MySQL 8.0
- Gradle 8.x

### 데이터베이스 설정
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/chess_dot_net
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 빌드 및 실행
```bash
./gradlew build
./gradlew bootRun
```

## 테스트

- JUnit 기반 단위 테스트
- JaCoCo를 통한 테스트 커버리지 분석
- 최소 80% 코드 커버리지 요구

## API 문서

### REST API
- `/api/users/*`: 사용자 관리 API
- `/api/rooms/*`: 방 관리 API

### WebSocket 엔드포인트
- `/ws`: 웹소켓 연결
- `/topic/rooms/{roomId}`: 방 상태 구독
- `/topic/chess.game.{roomId}`: 게임 상태 구독

## 작성자
- 전종영