# Test Design for Chessboard
   
1. 최초의 보드 상태에서 모든 move 테스트
   - 가능한 모든 `Piece::testAndMove()` 호출
        1. 시작 포지션에서 모든 경우의 수 `시작 위치 X 도착 위치` 64 * 64개 호출
        2. 이때 가능한 legal 인 경우의 수는 오직 폰의 1~2칸 전진 + 나이트 움직임이다.
        3. 어떤 move 가 legal 인지 판단하기 위해서 2번의 Move 들은 특별히 TreeSet 에 저장한다.
        4. 첫 턴에서 white 가 legal 인 수를 둔 다음 black 의 턴에 동일하게 1번을 테스트.   
        

2. 이외의 테스트 케이스들
   - 일부 테스트 케이스들이 있습니다.
   - 각 테스트 케이스에 대한 자세한 설명은 필요하면 나중에 적겠습니다.

3. 일부 함수들의 간략한 설명
   - `parsePos()`: 대수 표기법에 따른 좌표 하나를 받아서 ChessboardPos 하나를 생성합니다.
   - `parseMove()`: 대수 표기법에 따른 좌표 두 개를 받아서 `ChessboardMoveForTest` 하나를 생성합니다.
   - `ChessboardForTest`: `tryMoveAndRollback` 메소드가 추가되어있습니다.
     - `tryMoveAndRollback`: `tryMovePiece` 와 동일한 값을 반환하지만, 성공하더라도 chessboard 에는 반영되지 않습니다. (롤백됩니다.)
   - `ChessboardMoveForTest`: 저장된 `move` 의 legal 여부를 마킹할 수 있습니다.
     - `clearIsLegal()`: `isLegal = false;` 로 처리한 후, 자기 자신을 반환합니다. (Fluent Interface)
   - `showBoardBrief()`: 주어진 `Chessboard`와 두 좌표로 현재 게임 상태를 간략하게 출력합니다.
   - `runMoves()`: `ChessboardMoveForTest` 의 리스트를 받고 시뮬레이션합니다. `tryMovePiece()` 의 결과가 
   각 move 의 `isLegal` 과 일치하는지 확인합니다. 