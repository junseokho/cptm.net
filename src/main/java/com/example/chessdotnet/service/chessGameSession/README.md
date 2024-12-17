Author: SONY-STRING   

Date: on first commit of this directory

# About `class Chessboard`
   
Chessboard has hierarchy like below.

   
`class Chessboard`   
-> `class Piece` (including derived classes)
 
> There are some states which are need to be changed when a move performed.   
   
That states are distributed among `class Chessboard` and `class Piece`.   
But, only `class Chessboard` is charge to manage them. You don't need worry that at **Pieces**.   
   
> Validate of a move

There are two types of things to check.
1. Valid Input
2. Legality of the move

"Valid move" in `Valid Input` is a move 
which is from request of client in normal way.   
In example, let a piece at `startPosition` of move as `P`.   
If `P` has different color compare to the player color of this turn,
That move is `invalid`. This is because client is **never** able to send this move
request as client's program restricts for user to send illegal move.   
At same time, I define this `valid` as do not check rule of moves.

At now, the concept of `legality of the move` is clear. To check Legality of the move,
you need to check only rule of moves.   
   
Now then, `Chessboard` is charge to validate `1` only and only if, while
`Piece` is charge to check `Legality of the move`.



# `class Chessboard`에 대하여

체스보드는 아래와 같은 계층 구조를 가지고 있습니다.

`class Chessboard`   
-> `class Piece` (상속받은 클래스 포함)

> 이동이 수행될 때 변경되어야 하는 몇 가지 상태가 있습니다.

그 상태들은 `class Chessboard`와 `class Piece`에 분산되어 있습니다. 하지만, 오직 `class Chessboard`만이 그것들을 관리할 책임이 있습니다. **Pieces**에서는 이것에 대해 걱정할 필요가 없습니다.

> 이동의 유효성 검사

체크해야 할 사항은 두 가지 유형이 있습니다.
1. 유효한 입력
2. 이동의 합법성

`유효한 입력`에서의 "유효한 이동"은 일반적인 방식으로 클라이언트의 요청에서 오는 이동입니다.
예를 들어, 이동의 `startPosition`에 있는 조각을 `P`라고 하면,
`P`가 이 턴의 플레이어 색과 다르다면, 그 이동은 `유효하지 않음`입니다. 이는 클라이언트의 프로그램이 사용자가 불법적인 이동을 보내지 못하도록 제한하기 때문에 클라이언트가 이 이동 요청을 **절대** 보낼 수 없기 때문입니다.
동시에, 이 `유효함`을 이동 규칙을 확인하지 않는 것으로 정의합니다.

이제 `이동의 합법성`의 개념이 명확해졌습니다. 이동의 합법성을 확인하려면, 이동 규칙만 확인하면 됩니다.

그러므로, `Chessboard`은 오직 `1`만 확인할 책임이 있으며, `Piece`는 `이동의 합법성`을 확인할 책임이 있습니다.