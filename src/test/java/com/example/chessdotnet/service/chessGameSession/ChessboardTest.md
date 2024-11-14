# Test Design for Chessboard
   
1. 최초의 보드 상태에서 모든 move 테스트
   - 가능한 모든 `Piece::testAndMove()` 호출
        1. 시작 포지션에서 모든 경우의 수 `시작 위치 X 도착 위치` 64 * 64개 호출
        2. 이때 가능한 legal 인 경우의 수는 오직 폰의 1~2칸 전진 + 나이트 움직임이다.
        3. 어떤 move 가 legal 인지 판단하기 위해서 2번의 Move 들은 특별히 TreeSet 에 저장한다.
        4. 첫 턴에서 white 가 legal 인 수를 둔 다음 black 의 턴에 동일하게 1번을 테스트.