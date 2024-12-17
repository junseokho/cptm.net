package com.example.chessdotnet.service.chessGameSession;

/**
 * 체스 게임에서 플레이어의 남은 시간을 관리하는 클래스입니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-16
 */
public class LeftTime {
    /**
     * 남은 초
     */
    public final int leftSeconds;

    /**
     * 남은 1/10초
     */
    public final int leftDeciseconds;

    /**
     * 새로운 LeftTime 인스턴스를 생성합니다.
     *
     * @param seconds 초 단위 시간
     * @param deciseconds 1/10초 단위 시간
     */
    public LeftTime(int seconds, int deciseconds) {
        // deciseconds가 10 이상이면 seconds로 변환
        this.leftSeconds = seconds + (deciseconds / 10);
        this.leftDeciseconds = deciseconds % 10;
    }

    /**
     * 소비된 시간을 반영하여 새로운 LeftTime 인스턴스를 생성합니다.
     *
     * @param spentSeconds 소비된 초 단위 시간
     * @param spentDeciseconds 소비된 1/10초 단위 시간
     * @return 갱신된 LeftTime 인스턴스
     */
    public LeftTime updateLeftTime(int spentSeconds, int spentDeciseconds) {
        int newDeciseconds = leftDeciseconds - spentDeciseconds;
        int newSeconds = leftSeconds - spentSeconds;

        // deciseconds가 음수면 seconds에서 차감
        while (newDeciseconds < 0) {
            newDeciseconds += 10;
            newSeconds--;
        }

        // seconds가 음수면 0으로 설정
        if (newSeconds < 0) {
            newSeconds = 0;
            newDeciseconds = 0;
        }

        return new LeftTime(newSeconds, newDeciseconds);
    }
}