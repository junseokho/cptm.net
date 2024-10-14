/**
 * @author 손의현(SONY_STRING)
 * @description 대국과 관련된 API들 입니다.*
 *
 * #todo 헤더.. 문제가 있는거 같음
 * #todo API 변경사항이 생길 것 같은데 아직 미적용되었습니다
 */


import axios from 'axios'

const axios_instanat = axios.create({
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
class MatchAPI {
    /**
     * @description 게임을 시작하지 않은 모든 대국 목록 정보를 가져옵니다.
     * @returns {Promise<axios.AxiosResponse<any>>} http 요청 결과
     */
    static get_available_matches () {
        return axios_instanat.get(
            `/api/rooms/available`,
        );
    }
    /**
     * @description 새로운 대국을 생성합니다.
     * @returns {Promise<axios.AxiosResponse<any>>} http 요청 결과
     */
    static create_new_match (match_title, match_creator_id) {
        return axios_instanat.post(
            `/api/rooms/create`,
            {
                title: match_title,
                hostId: match_creator_id
            },
        );
    }
    /**
     * @description 대국에 참여합니다.
     * @returns {Promise<axios.AxiosResponse<any>>} http 요청 결과
     */
    static join_match (match_id, join_user_id) {
        return axios_instanat.post(
            `/api/rooms/${match_id}/join`,
            {
                userId: join_user_id
            }
        );
    }
}

export {MatchAPI};