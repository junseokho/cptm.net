/**
 * @author 손의현(SONY_STRING)
 * @description 대국방과 관련된 API들 입니다.*
 *
 * #todo 헤더.. 문제가 있는거 같음
 * #todo API 변경사항이 생길 것 같은데 아직 미적용되었습니다
 */


import axios from 'axios'

const axios_instant = axios.create({
    baseURL: 'http://localhost:5173',
    auth: {
        username: 'test',
        password: 'test'
    }
});

/**
 * @description 체스 대국들과 관련된 API 목록입니다.
 * @author 손의현(SONY_STRING)
 * */
class RoomAPI {
    /**
     * @description 게임을 시작하지 않은 모든 대국 목록 정보를 가져옵니다.
     * @returns {Promise<axios.AxiosResponse<any>>} http 요청 결과
     */
    static get_playable_rooms () {
        return axios_instant.get(
            `/api/rooms/playable`,
        );
    }
    /**
     * @description 새로운 대국을 생성합니다.
     * @returns {Promise<axios.AxiosResponse<any>>} http 요청 결과
     */
    static createRoom(hostId, timeControlMin, timeControlSec, timeControlInc) {
        return axios_instant.post(
            `/api/rooms/create`,
            {
                hostId: hostId,
                timeControlMin: timeControlMin,
                timeControlSec: timeControlSec,
                timeControlInc: timeControlInc
            },
        );
    }
    /**
     * @description 대국에 참여합니다.
     * @returns {Promise<axios.AxiosResponse<any>>} http 요청 결과
     */
    static joinRoom(roomId, userId) {
        return axios_instant.post(
            `/api/rooms/join`,
            {
                roomId: roomId,
                userId: userId
            }
        );
    }

    static startGame(roomId, userId) {
        return axios_instant.post(
            `/api/rooms/startGame`,
            {
                roomId: roomId,
                userId: userId
            }
        );
    }

    static deleteRoom(roomId, userId) {
        return axios_instant.delete(
            `api/rooms/` + roomId.toString() + `?userId=` + userId.toString(),
        );
    }
}

export {RoomAPI};